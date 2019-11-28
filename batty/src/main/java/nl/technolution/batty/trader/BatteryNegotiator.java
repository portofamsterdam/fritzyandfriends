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
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
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
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.OrderHelper;
import nl.technolution.fritzy.wallet.event.EventLogger;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;
import nl.technolution.protocols.efi.ActuatorBehaviour;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.RunningMode;
import nl.technolution.protocols.efi.StorageContinuousRunningMode;
import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.StorageUpdate;
import nl.technolution.protocols.efi.util.AbstractCustomerEnergyManager;
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
    private Instant nextTradeStart;

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
     * 
     * @throws FritzyApiException
     */
    public void evaluate() throws FritzyApiException {

        // Can only do something if we know our capabilities
        if (fillLevel == null || systemDescription == null) {
            log.warn("Waiting for state of charge");
            return;
        }

        // Get balance
        IFritzyApi market = Services.get(IFritzyApiFactory.class).build();
        EventLogger events = new EventLogger(market);
        FritzyBalance balance = market.balance();
        events.logBalance(balance);

        // Get max capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceId deviceId = resourceManager.getDeviceId();
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        events.logLimitActor(deviceCapacity.getGridConnectionLimit());

        checkOpenOrders(market);
        // Note check open order may set nextTradeStart
        if (nextTradeStart != null && Instant.now().isBefore(nextTradeStart)) {
            log.debug("Trading done for this period");
            return;
        }

        Orders orders = market.orders().getOrders();
        for (Record record : orders.getRecords()) {
            WebOrder order = record.getOrder();
            if (order.getMakerAddress().equals(market.getAddress())) {
                continue; // This is batty himself
            }

            if (OrderHelper.isAccepted(order)) {
                continue; // order is already filled (shouldn't happen?)
            }

            if (!isInterestingOrder(order, deviceCapacity)) {
                log.debug("Order {} not interesting", order.getHash());
                continue;
            }
            log.debug("Interesting order found {}", order.getHash());

            if (order.getTakerAssetData().equals(EContractAddress.EUR.getContractName()) &&
                    balance.getEur().doubleValue() < new BigDecimal(order.getTakerAssetAmount()).doubleValue()) {
                log.warn("Can't afford {} for order, balance is {}", order.getTakerAssetAmount(), balance.getEur());
                continue;
            }

            OrderReward reward = netty.getOrderReward(market.getAddress(), order.getHash());
            if (!checkAcceptOffer(order, reward)) {
                continue;
            }
            log.debug("Reward {} is adequate at {}ct, accepting order", reward.getRewardId(), reward.getReward());
            acceptOrder(market, netty, order, reward);
            return;
        }

        createOrder(market, balance, deviceCapacity);
    }

    private void acceptOrder(IFritzyApi market, INettyApi netty, WebOrder order, OrderReward reward)
            throws FritzyApiException {
        log.info("accepting order {} receiving {}{} spending {}{}", order.getHash(), order.getMakerAssetAmount(),
                order.getMakerAssetData(), order.getTakerAssetAmount(), order.getTakerAssetData());
        if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName())) {
            log.info("minting {}{} for order {}", order.getTakerAssetAmount(), order.getTakerAssetData(),
                    order.getHash());
            market.mint(market.getAddress(), new BigDecimal(order.getTakerAssetAmount()), EContractAddress.KWH);
        }

        String txId = market.fillOrder(order.getHash());
        log.warn("claiming reward {}", reward.getRewardId());
        netty.claim(txId, reward.getRewardId());

        activeBattyOrders.add(new OrderExecutor(order.getHash()));
        cancelExistingBattyOrders(market);
    }

    private void checkOpenOrders(IFritzyApi market) throws FritzyApiException {
        Iterator<OrderExecutor> itr = activeBattyOrders.iterator();
        while (itr.hasNext()) {
            OrderExecutor activeBattyOrder = itr.next();
            EOrderCommand orderState = activeBattyOrder.evaluate(resourceManager, systemDescription, fillLevel);
            if (orderState == EOrderCommand.FINISHED) {
                itr.remove();
            }
            if (activeBattyOrder.getStartTs() != null && activeBattyOrder.getStartTs().isAfter(Instant.now())) {
                // There is an accepted order pending. stop taking new orders
                nextTradeStart = activeBattyOrder.getStartTs();
                cancelExistingBattyOrders(market);
            }
        }
    }

    private void createOrder(IFritzyApi market, FritzyBalance balance, DeviceCapacity deviceCapacity)
            throws FritzyApiException {
        ApxPrice apxPrice = Endpoints.get(IAPXPricesApi.class).getNextQuarterHourPrice();
        createBuyKWhOrder(market, balance, deviceCapacity, apxPrice);
        createSellOrder(market, deviceCapacity, apxPrice);
    }

    private void createSellOrder(IFritzyApi market, DeviceCapacity deviceCapacity,
            ApxPrice apxPrice) throws FritzyApiException {
        if (openSellOrderHash != null) {
            WebOrder sellOrder = market.order(openSellOrderHash);
            if (sellOrder != null) {
                return;
            }
        }
        if (fillLevel <= 20) {
            log.debug("Battery level too low to sell energy");
            return;
        }
        double wq = getWattPerQuarterFromRunningMode(BattyResourceHelper.DISCHARGE_LABEL, systemDescription,
                fillLevel);

        // check maximum usage on grid
        double maxWq = deviceCapacity.getGridConnectionLimit() * 230d / 4;
        if (maxWq < wq) {
            wq = maxWq;
        }

        double kWhPrice = apxPrice.getPrice() + (config.getBuyMargin() / 100d);
        double orderPrice = (wq / 1000) * kWhPrice;

        BigDecimal kWhToSell = BigDecimal.valueOf(wq / 1000d);
        if (market.balance().getKwh().doubleValue() < kWhToSell.doubleValue()) {
            double kWhToMint = kWhToSell.doubleValue() - market.balance().getKwh().doubleValue();
            market.mint(market.getAddress(), BigDecimal.valueOf(kWhToMint), EContractAddress.KWH);
        }
        
        openSellOrderHash = market.createOrder(EContractAddress.KWH, EContractAddress.EUR,
                BigDecimal.valueOf(orderPrice), kWhToSell);
        activeBattyOrders.add(new OrderExecutor(openSellOrderHash));
    }

    private void createBuyKWhOrder(IFritzyApi market, FritzyBalance balance, DeviceCapacity deviceCapacity,
            ApxPrice apxPrice) throws FritzyApiException {
        if (openBuyOrderHash != null) {
            WebOrder buyOrder = market.order(openSellOrderHash);
            if (buyOrder != null) {
                return;
            }
        }
        if (fillLevel >= 80) {
            log.debug("Battery level too high to buy energy");
            return;
        }
        double wq = getWattPerQuarterFromRunningMode(BattyResourceHelper.CHARGE_LABEL, systemDescription,
                fillLevel);

        // check maximum usage on grid
        double maxWq = deviceCapacity.getGridConnectionLimit() * 230d / 4d;
        if (maxWq < wq) {
            wq = maxWq;
        }

        double kWhPrice = apxPrice.getPrice() - (config.getBuyMargin() / 100d);
        double orderPrice = (wq / 1000d) * kWhPrice;

        if (orderPrice > balance.getEur().doubleValue()) {
            log.info("No funds (balance is {}) to create order that costs {}", balance.getEur(), orderPrice);
            return;
        }

        openBuyOrderHash = market.createOrder(EContractAddress.EUR, EContractAddress.KWH,
                BigDecimal.valueOf(orderPrice), BigDecimal.valueOf(wq / 1000d));
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
                // NOTE only supports one running mode element for now
                Preconditions.checkState(scrm.getContinuousRunningModeElement().size() == 1);
                return getKiloWattPerQuarterFromRunningMode(scrm.getContinuousRunningModeElement().get(0), fillLevel);
            }
        }
        throw new IllegalStateException();
    }

    /**
     * @param market
     * @throws FritzyApiException
     */
    private void cancelExistingBattyOrders(IFritzyApi market) throws FritzyApiException {
        // Cancel existing orders
        cancelOrder(market, openBuyOrderHash);
        openBuyOrderHash = null;
        cancelOrder(market, openSellOrderHash);
        openSellOrderHash = null;
    }

    private static void cancelOrder(IFritzyApi market, @Nullable String hash) throws FritzyApiException {
        if (hash == null) {
            return;
        }
        WebOrder order = market.order(hash);
        if (!OrderHelper.isAccepted(order)) {
            market.cancelOrder(hash);
        }
    }

    private boolean isInterestingOrder(WebOrder order, DeviceCapacity deviceCapacity) {
        log.debug("Checking order {}", order.getHash());
        // Charging order
        if (order.getMakerAssetData().equals(EContractAddress.KWH.getContractName())) {

            BigDecimal kWh = new BigDecimal(order.getMakerAssetAmount());
            log.debug("Order for buying {} kWh (Charing)", kWh);

            return calculateOrder(order, deviceCapacity, kWh);
        }
        // Discharging order
        if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName())) {
            // Discharge with negative kWh's
            BigDecimal kWh = new BigDecimal(order.getTakerAssetAmount());
            log.debug("Order for selling {} kWh (Discharing)", kWh);

            return calculateOrder(order, deviceCapacity, kWh);

        }
        return false;
    }

    private boolean calculateOrder(WebOrder order, DeviceCapacity devCapacity, BigDecimal kWh) {
        BigDecimal kWq = kWh.multiply(BigDecimal.valueOf(4)); // kWh to be generated in 1/4 hour so multiply by 4
        double maxAmps = kWq.doubleValue() * 1000d / 230d;
        if (maxAmps > devCapacity.getGridConnectionLimit()) {
            // not allowed use this much energy
            log.warn("Order {} doesn't fit capacity of batty {}", order.getHash(),
                    devCapacity.getGridConnectionLimit());
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
        log.debug("No runningmode found that fits handling {}{}", kWq.doubleValue(),
                EContractAddress.KWH.getContractName());
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
                    Log.getLogger().debug("Capacity for runningmode {} is {}", rm.getLabel(), capacityWq);
                    if (kiloWattPerQuarter > (capacityWq / 1000)) {
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
        return capacityWh / 4d;
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {
        IAPXPricesApi pricesApi = Endpoints.get(IAPXPricesApi.class);
        ApxPrice apxPriceObj = pricesApi.getNextQuarterHourPrice();
        double marketPrice = apxPriceObj.getPrice();
        log.debug("Market price is {}, reward is {}", marketPrice, reward.getReward());

        if (order.getTakerAssetData().equals(EContractAddress.EUR.getContractName())) {
            double orderPrice = new BigDecimal(order.getTakerAssetAmount()).doubleValue();
            double totalIncome = orderPrice + reward.getReward();
            if (totalIncome > marketPrice + (config.getBuyMargin() / 100d)) {
                log.debug("Buying energy is worth it (sellMargin is {}), receiving {}", config.getBuyMargin(),
                        totalIncome);
                return true;
            }
            log.debug("Buying energy not worth it (sellMargin is {}), receiving {}", config.getBuyMargin(),
                    totalIncome);

        }

        if (order.getMakerAssetData().equals(EContractAddress.EUR.getContractName())) {
            double orderPrice = new BigDecimal(order.getMakerAssetAmount()).doubleValue();
            double totalPrice = orderPrice - reward.getReward();
            if (totalPrice < marketPrice - (config.getSellMargin() / 100d)) {
                log.debug("Selling energy is worth it (buyMargin is {}), spending {}", config.getSellMargin(),
                        totalPrice);
                return true;
            }
            log.debug("Selling energy not worth it (buyMargin is {}), speding {}", config.getSellMargin(),
                    totalPrice);
        }
        return false;
    }

    public Double getFillLevel() {
        return fillLevel;
    }

    @Override
    public DeviceId getDeviceId() {
        return new DeviceId(config.getDeviceId());
    }
}
