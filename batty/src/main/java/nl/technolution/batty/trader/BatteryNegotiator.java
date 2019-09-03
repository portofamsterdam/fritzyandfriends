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
            if (!isInterestingOrder(order, balance, deviceCapacity)) {
                continue;
            }
            log.debug("Interesting order found {}", order.getHash());

            OrderReward reward = netty.getOrderReward(market.getAddress(), order.getHash());
            if (!checkAcceptOffer(order, reward)) {
                continue;
            }
            log.debug("Reward {} is adequate at {}, accepting order", reward.getRewardId(), reward.getReward());

            String txId = market.fillOrder(order.getHash());
            netty.claim(txId, reward.getRewardId());

            log.debug("Instructing device");
            instructDevice(order);
        }
    }

    private boolean isInterestingOrder(WebOrder order, FritzyBalance balance, DeviceCapacity deviceCapacity) {
        log.debug("Checking order {}", order.getHash());
        // Charging order
        if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName())) {

            BigDecimal kWh = new BigDecimal(order.getTakerAssetAmount());
            log.debug("Order for buying {} kWh (Charing)", kWh);

            if (isRoomForKWh(config.getCapactity(), fillLevel, kWh.doubleValue())) {
                return calculateOrder(order, balance, deviceCapacity, kWh);
            }

        }
        // Discharging order
        if (order.getMakerAssetData().equals(EContractAddress.KWH.getContractName())) {
            // Discharge with negative kWh's
            BigDecimal kWh = new BigDecimal(order.getTakerAssetAmount()).multiply(new BigDecimal(-1));
            log.debug("Order for selling {} kWh (Discharing)", kWh);

            if (isRoomForKWh(config.getCapactity(), fillLevel, kWh.doubleValue())) {
                return calculateOrder(order, balance, deviceCapacity, kWh);
            }
        }
        return false;
    }

    private boolean isRoomForKWh(double capacity, double fillLevel, double kWh) {
        double fillLevelKwh = capacity * (fillLevel / 100d);
        double newFillLevel = fillLevelKwh + kWh;
        if (newFillLevel > capacity || newFillLevel < 0) {
            log.debug("Can't fit {} kWh, already charged to {}/{}", kWh, fillLevelKwh, config.getCapactity());
            return false;
        }
        return true;
    }

    private boolean calculateOrder(WebOrder order, FritzyBalance balance, DeviceCapacity devCapacity, BigDecimal kWh) {
        BigDecimal kWq = kWh.multiply(BigDecimal.valueOf(4));
        double maxAmps = kWq.doubleValue() * 1000d / 230d;
        if (maxAmps > devCapacity.getGridConnectionLimit()) {
            // not allowed use this much energy
            log.warn("Order {} doesn't fit capacity of batty {}", order, devCapacity.getGridConnectionLimit());
            return false;
        }
        for (ActuatorBehaviour act : systemDescription.getActuatorBehaviours().getActuatorBehaviour()) {
            if (kWq.doubleValue() > 0 &&
                    checkBehavious(kWq.doubleValue(), act, fillLevel, BattyResourceHelper.CHARGE_LABEL)) {
                return true;
            }
            if (kWq.doubleValue() < 0 &&
                    checkBehavious(kWq.doubleValue(), act, fillLevel, BattyResourceHelper.CHARGE_LABEL)) {
                return true;
            }

        }
        return false;
    }

    static boolean checkBehavious(double kileWattPerQuarter, ActuatorBehaviour act, double fillLevel, String rmLabel) {
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

                    if (kileWattPerQuarter < 0 && kileWattPerQuarter < capacityWq) {
                        continue; // Cannot discharge this fast at this charge level
                    }
                    if (kileWattPerQuarter > 0 && kileWattPerQuarter > capacityWq) {
                        continue; // Cannot charge this fast at this charge level
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
        IAPXPricesApi pricesApi = Services.get(IAPXPricesApi.class);
        ApxPrice apxPriceObj = pricesApi.getNextQuarterHourPrice();
        double marketPrice = apxPriceObj.getPrice();
        log.debug("Market price is {}, reward is {}", marketPrice, reward.getReward());

        if (order.getTakerAssetData().equals(EContractAddress.EUR.getContractName())) {
            double orderPrice = new BigDecimal(order.getTakerAssetAmount()).doubleValue();
            double totalIncome = orderPrice + reward.getReward();
            if (totalIncome > marketPrice + config.getSellMargin()) {
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

            if (totalPrice < marketPrice - config.getBuyMargin()) {
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
        ActuatorInstruction actInstruction = new ActuatorInstruction();
        actInstruction.setActuatorId(BattyResourceHelper.ACTUATOR_ID);
        actInstruction.setRunningModeId(EBattyInstruction.CHARGE.getRunningModeId());
        actInstruction.setStartTime(Efi.calendarOfInstant(Efi.getNextQuarter()));
        actInstuctions.getActuatorInstruction().add(actInstruction);
        instruction.setActuatorInstructions(actInstuctions);
        resourceManager.instruct(instruction);
    }

    public Double getFillLevel() {
        return fillLevel;
    }
}
