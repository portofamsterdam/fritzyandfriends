/*
 (C) COPYRIGHT TECHNOLUTION BV, GOUDA NL
| =======          I                   ==          I    =
|    I             I                    I          I
|    I   ===   === I ===  I ===   ===   I  I    I ====  I   ===  I ===
|    I  /   \ I    I/   I I/   I I   I  I  I    I  I    I  I   I I/   I
|    I  ===== I    I    I I    I I   I  I  I    I  I    I  I   I I    I
|    I  \     I    I    I I    I I   I  I  I   /I  \    I  I   I I    I
|    I   ===   === I    I I    I  ===  ===  === I   ==  I   ===  I    I
|                 +---------------------------------------------------+
+----+            |  +++++++++++++++++++++++++++++++++++++++++++++++++|
     |            |             ++++++++++++++++++++++++++++++++++++++|
     +------------+                          +++++++++++++++++++++++++|
                                                        ++++++++++++++|
                                                                 +++++|
 */
package nl.technolution.batty.trader;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.collect.Lists;

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.apis.exxy.ApxPrice;
import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.batty.app.BattyConfig;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.ActuatorBehaviour;
import nl.technolution.protocols.efi.ActuatorInstruction;
import nl.technolution.protocols.efi.ActuatorInstructions;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.RunningMode;
import nl.technolution.protocols.efi.StorageContinuousRunningMode;
import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.StorageUpdate;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class BatteryNegotiator extends AbstractCustomerEnergyManager<StorageRegistration, StorageUpdate> {

    private final Logger log = Log.getLogger();
    private final BattyResourceManager resourceManager;
    private final BattyConfig config;

    private Double fillLevel;

    private StorageSystemDescription systemDescription;

    private List<OrderExecutor> activeBattyOrders = Lists.newArrayList();
    private String openBuyOrderHash;
    private String openSellOrderHash;

    /**
     *
     * @param config config used for trading
     * @param resourceManager to control devices
     * @param config
     */
    public BatteryNegotiator(BattyResourceManager resourceManager, BattyConfig config) {
        this.resourceManager = resourceManager;
        this.config = config;
    }

    @Override
    public Instruction flexibilityUpdate(StorageUpdate storageStatus) {
        if (storageStatus instanceof StorageStatus) {
            fillLevel = ((StorageStatus)storageStatus).getCurrentFillLevel();
            log.debug("Fill level set to {}", fillLevel);
        }
        if (storageStatus instanceof StorageSystemDescription) {
            systemDescription = StorageSystemDescription.class.cast(storageStatus);
        }
        return Efi.build(StorageInstruction.class, getDeviceId());

    }

    /**
     * Call periodicly to evaluate market changes
     */
    public void evaluate() {

        // Can only do something if we know our capabilities
        if (fillLevel == null || systemDescription == null) {
            log.warn("Waiting for state of charge");
            return;
        }

        checkOpenOrders();

        // Get balance
        IFritzyApi market = Services.get(IFritzyApiFactory.class).build();
        FritzyBalance balance = market.balance();
        market.log(EEventType.BALANCE, balance.getEur().toPlainString(), null);

        // Get max capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceId deviceId = resourceManager.getDeviceId();
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        market.log(EEventType.LIMIT_ACTOR, Double.toString(deviceCapacity.getGridConnectionLimit()), null);

        Orders orders = market.orders().getOrders();
        for (Record record : orders.getRecords()) {
            WebOrder order = record.getOrder();
            if (order.getHash().equals(openBuyOrderHash) || order.getHash().equals(openSellOrderHash)) {
                continue;
            }

            if (!isInterestingOrder(order, balance, deviceCapacity)) {
                continue;
            }
            log.debug("Interesting order found {}", order.getHash());

            OrderReward reward = netty.getOrderReward(market.getAddress(), order.getHash());
            if (!checkAcceptOffer(order, reward)) {
                continue;
            }
            log.debug("Reward {} is adequate at {}, accepting order", reward.getRewardId(), reward.getReward());

            if (order.getTakerAssetData().equals(EContractAddress.EUR.getContractName())) {
                if (balance.getEur().doubleValue() < new BigDecimal(order.getTakerAssetAmount()).doubleValue()) {
                    log.warn("Can't afford {} for order, balance is {}", order.getTakerAssetAmount(), balance.getEur());
                    continue;
                }
            } else if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName())) {
                log.warn("minting {}{}", order.getTakerAssetAmount(), order.getTakerAssetData());
                market.mint(market.getAddress(), new BigDecimal(order.getTakerAssetAmount()), EContractAddress.KWH);
            }
            
            String txId = market.fillOrder(order.getHash());
            netty.claim(txId, reward.getRewardId());

            activeBattyOrders.add(new OrderExecutor(order.getHash()));
            cancelExistingOrders(market);
            return;
        }

        createOrder(market, balance, deviceCapacity);
    }

    private void checkOpenOrders() {
        for (OrderExecutor activeBattyOrder : activeBattyOrders) {
            activeBattyOrder.evaluate(resourceManager, systemDescription, fillLevel);
        }

    }

    private void createOrder(IFritzyApi market, FritzyBalance balance, DeviceCapacity deviceCapacity) {
        ApxPrice apxPrice = Endpoints.get(IAPXPricesApi.class).getNextQuarterHourPrice();
        createBuyOrder(market, balance, deviceCapacity, apxPrice);
        createSellOrder(market, balance, deviceCapacity, apxPrice);
    }

    private void createSellOrder(IFritzyApi market, FritzyBalance balance, DeviceCapacity deviceCapacity,
            ApxPrice apxPrice) {
        if (openSellOrderHash != null) {
            WebOrder sellOrder = market.order(openSellOrderHash);
            if (sellOrder != null) {
                return;
            }
        }
        if (fillLevel < 20) {
            log.debug("Battery level too low to sell energy");
        }
        double wq = getWattPerQuarterFromRunningMode(BattyResourceHelper.DISCHARGE_LABEL, systemDescription,
                fillLevel);

        // check maximum usage on grid
        double maxWq = deviceCapacity.getGridConnectionLimit() * 230d / 4;
        if (maxWq > wq) {
            wq = maxWq;
        }

        double kWhPrice = apxPrice.getPrice() + config.getBuyMargin();
        double orderPrice = wq * kWhPrice;

        openSellOrderHash = market.createOrder(EContractAddress.EUR, EContractAddress.KWH, new BigDecimal(wq),
                new BigDecimal(orderPrice));
        activeBattyOrders.add(new OrderExecutor(openSellOrderHash));
    }

    private void createBuyOrder(IFritzyApi market, FritzyBalance balance, DeviceCapacity deviceCapacity,
            ApxPrice apxPrice) {
        if (openBuyOrderHash != null) {
            WebOrder buyOrder = market.order(openSellOrderHash);
            if (buyOrder != null) {
                return;
            }
        }
        double wq = getWattPerQuarterFromRunningMode(BattyResourceHelper.CHARGE_LABEL, systemDescription,
                fillLevel);

        // check maximum usage on grid
        double maxWq = deviceCapacity.getGridConnectionLimit() * 230d / 4;
        if (maxWq > wq) {
            wq = maxWq;
        }

        double kWhPrice = apxPrice.getPrice() - config.getBuyMargin();
        double orderPrice = wq * 1000 * kWhPrice;

        openBuyOrderHash = market.createOrder(EContractAddress.KWH, EContractAddress.EUR, new BigDecimal(wq),
                new BigDecimal(orderPrice));
        activeBattyOrders.add(new OrderExecutor(openBuyOrderHash));
    }

    static double getWattPerQuarterFromRunningMode(String runningModeLabel,
            StorageSystemDescription storageSystemDescription, double fillLevel) {
        for (ActuatorBehaviour act : storageSystemDescription.getActuatorBehaviours().getActuatorBehaviour()) {
            for (RunningMode rm : act.getRunningModes().getDiscreteRunningModeOrContinuousRunningMode()) {
                if (!rm.getLabel().equals(runningModeLabel)) {
                    continue;
                }
                StorageContinuousRunningMode scrm = StorageContinuousRunningMode.class.cast(rm);
                for (ContinuousRunningModeElement crm : scrm.getContinuousRunningModeElement()) {
                    return getKiloWattPerQuarterFromRunningMode(crm, fillLevel);
                }
            }
        }
        throw new IllegalStateException();
    }

    /**
     * @param market
     */
    private void cancelExistingOrders(IFritzyApi market) {
        // Cancel existing orders
        if (openBuyOrderHash != null) {
            market.cancelOrder(openBuyOrderHash);
        }
        if (openSellOrderHash != null) {
            market.cancelOrder(openSellOrderHash);
        }
    }

    private boolean isInterestingOrder(WebOrder order, FritzyBalance balance, DeviceCapacity deviceCapacity) {
        log.debug("Checking order {}", order.getHash());
        // Charging order
        if (order.getMakerAssetData().equals(EContractAddress.KWH.getContractName())) {

            BigDecimal kWh = new BigDecimal(order.getMakerAssetAmount());
            log.debug("Order for buying {} kWh (Charing)", kWh);

            return calculateOrder(order, balance, deviceCapacity, kWh);
        }
        // Discharging order
        if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName())) {
            // Discharge with negative kWh's
            BigDecimal kWh = new BigDecimal(order.getTakerAssetAmount());
            log.debug("Order for selling {} kWh (Discharing)", kWh);

            return calculateOrder(order, balance, deviceCapacity, kWh);

        }
        return false;
    }

    private boolean calculateOrder(WebOrder order, FritzyBalance balance, DeviceCapacity devCapacity, BigDecimal kWh) {
        BigDecimal kWq = kWh.multiply(BigDecimal.valueOf(4)); // kWh to be generated in 1/4 hour so multiply by 4
        double maxAmps = kWq.doubleValue() * 1000d / 230d;
        if (maxAmps > devCapacity.getGridConnectionLimit()) {
            // not allowed use this much energy
            log.warn("Order {} doesn't fit capacity of batty {}", order, devCapacity.getGridConnectionLimit());
            return false;
        }
        for (ActuatorBehaviour act : systemDescription.getActuatorBehaviours().getActuatorBehaviour()) {
            if (order.getMakerAssetData().equals(EContractAddress.KWH.getContractName()) &&
                    checkBehavious(kWq.doubleValue(), act, fillLevel, BattyResourceHelper.CHARGE_LABEL)) {
                log.debug("Charging {}{} fits ", kWq.doubleValue(), order.getMakerAssetData());
                return true;
            }

            if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName()) &&
                    checkBehavious(kWq.doubleValue(), act, fillLevel, BattyResourceHelper.DISCHARGE_LABEL)) {
                log.debug("Discharging {}{} fits ", kWq.doubleValue(), order.getMakerAssetData());
                return true;
            }

        }
        log.debug("No runningmode found that fits handling {}{}", kWq.doubleValue(), order.getMakerAssetData());
        return false;
    }

    static boolean checkBehavious(double kiloWattPerQuarter, ActuatorBehaviour act, double fillLevel, String rmLabel) {
        for (RunningMode rm : act.getRunningModes().getDiscreteRunningModeOrContinuousRunningMode()) {
            if (rm.getLabel().equals(rmLabel) && rm instanceof StorageContinuousRunningMode) {
                StorageContinuousRunningMode scrm = StorageContinuousRunningMode.class.cast(rm);
                for (ContinuousRunningModeElement m : scrm.getContinuousRunningModeElement()) {
                    // running mode not relevant for this fill level
                    if (fillLevel < m.getFillLevelLowerBound() || fillLevel > m.getFillLevelUpperBound()) {
                        continue;
                    }
                    // capacity of runningmode is negative when discharging
                    double capacityWq = getKiloWattPerQuarterFromRunningMode(m, fillLevel);
                    if (kiloWattPerQuarter > capacityWq) {
                        continue; // Cannot discharge this fast at this charge level
                    }
                    return true;
                }
            }
        }
        return false;
    }

    static double getKiloWattPerQuarterFromRunningMode(ContinuousRunningModeElement m, double fillLevel) {
        double capacityRange = m.getUpperBound().getElectricalPower() -
                m.getLowerBound().getElectricalPower();
        double startofRangeFillLevel = fillLevel - m.getFillLevelLowerBound();
        double fillLevelRange = m.getFillLevelUpperBound() - m.getFillLevelLowerBound();
        double positionInRange = startofRangeFillLevel / fillLevelRange;
        double capacityInRange = capacityRange * positionInRange;
        double capacityWh = m.getLowerBound().getElectricalPower() + capacityInRange;
        double capacityWq = capacityWh / 4;
        return capacityWq;
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {
        IAPXPricesApi pricesApi = Endpoints.get(IAPXPricesApi.class);
        ApxPrice apxPriceObj = pricesApi.getNextQuarterHourPrice();
        double marketPrice = apxPriceObj.getPrice();
        log.debug("Market price is {}, reward is {}", marketPrice, reward.getReward());

        if (order.getTakerAssetData().equals(EContractAddress.EUR.getContractName())) {
            double orderPrice = new BigDecimal(order.getTakerAssetAmount()).doubleValue();
            double totalIncome = orderPrice + reward.getReward();
            if (totalIncome > marketPrice + (config.getSellMargin() / 100d)) {
                log.debug("Selling energy is worth it (sellMargin is {}), receiving {}", config.getSellMargin(),
                        totalIncome);
                return true;
            }
            log.debug("Selling energy not worth it (sellMargin is {}), receiving {}", config.getSellMargin(),
                        totalIncome);

        }

        if (order.getMakerAssetData().equals(EContractAddress.EUR.getContractName())) {
            double orderPrice = new BigDecimal(order.getMakerAssetAmount()).doubleValue();
            double totalPrice = orderPrice - reward.getReward();
            log.debug("Market price is {}, reward is {}", marketPrice, reward.getReward());

            if (totalPrice < marketPrice - (config.getBuyMargin() / 100d)) {
                log.debug("Buying energy is worth it (buyMargin is {}), spending {}", config.getBuyMargin(),
                        totalPrice);
                return true;
            }
            log.debug("Buying energy not worth it (buyMargin is {}), speding {}", config.getBuyMargin(),
                    totalPrice);
        }
        return false;
    }

    private void instructDevice(WebOrder order) {
        StorageInstruction instruction = Efi.build(StorageInstruction.class, getDeviceId());
        ActuatorInstructions actInstuctions = new ActuatorInstructions();

        // Charge instruction
        ActuatorInstruction actInstruction = new ActuatorInstruction();
        actInstruction.setActuatorId(BattyResourceHelper.ACTUATOR_ID);
        for (ActuatorBehaviour ab : systemDescription.getActuatorBehaviours().getActuatorBehaviour()) {
            for (RunningMode mode : ab.getRunningModes().getDiscreteRunningModeOrContinuousRunningMode()) {
                if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName()) &&
                        mode.getLabel().equals(BattyResourceHelper.CHARGE_LABEL)) {
                    actInstruction.setRunningModeId(mode.getId());
                }
                if (order.getMakerAssetData().equals(EContractAddress.KWH.getContractName()) &&
                        mode.getLabel().equals(BattyResourceHelper.DISCHARGE_LABEL)) {
                    actInstruction.setRunningModeId(mode.getId());
                }
            }
        }
        XMLGregorianCalendar battyActStartTs = Efi.calendarOfInstant(Efi.getNextQuarter());
        actInstruction.setStartTime(battyActStartTs);
        actInstuctions.getActuatorInstruction().add(actInstruction);

        // Stop instruction

        instruction.setActuatorInstructions(actInstuctions);
        resourceManager.instruct(instruction);
    }

    public Double getFillLevel() {
        return fillLevel;
    }

    @Override
    public DeviceId getDeviceId() {
        return new DeviceId(config.getDeviceId());
    }
}
