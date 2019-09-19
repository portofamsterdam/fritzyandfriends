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
package nl.technolution.fritzy.marketnegotiator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.EventLogger;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.ActuatorBehaviour;
import nl.technolution.protocols.efi.ActuatorInstruction;
import nl.technolution.protocols.efi.ActuatorInstructions;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.RunningMode;
import nl.technolution.protocols.efi.StorageDiscreteRunningMode;
import nl.technolution.protocols.efi.StorageDiscreteRunningMode.DiscreteRunningModeElement;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.StorageUpdate;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class FritzyNegotiator extends AbstractCustomerEnergyManager<StorageRegistration, StorageUpdate> {
    private static final Logger LOG = Log.getLogger();

    private FritzyResourceManager resourceManager;
    private IFritzyApi cachedFritzyApi;
    private final double maxMargin;
    private final double marketPriceStartOffset;

    private double fillLevel;
    private double neededKWh;
    private int actuatorId;

    private double myPrice;

    private Map<Integer, RunningMode> runningModes = new HashMap<>();

    private int runningModeOnId;
    private int runningModeOffId;
    // Initialize at 'off' because no energy was purchased for current period
    private int currentRoundRunningModeId = runningModeOffId;
    private int nextRoundRunningModeId = runningModeOffId;

    public FritzyNegotiator(FritzyConfig config, FritzyResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        marketPriceStartOffset = config.getMarketPriceStartOffset();
        this.maxMargin = config.getMaxMargin();
    }

    /**
     * Extend abstract implementation.
     */
    @Override
    public final void flexibilityRegistration(StorageRegistration flexibilityRegistration) {
        super.flexibilityRegistration(flexibilityRegistration);

        if (flexibilityRegistration.getActuators().getActuator().size() > 1) {
            // other methods expect only 1 actuator
            throw new Error("More than 1 actuator configured.");
        }
        actuatorId = flexibilityRegistration.getActuators().getActuator().get(0).getId();
    }

    /**
     * @param storageSystemDescription
     */
    public void storageSystemDescription(StorageSystemDescription storageSystemDescription) {
        for (ActuatorBehaviour actuatorBehaviour : storageSystemDescription.getActuatorBehaviours()
                .getActuatorBehaviour()) {
            if (actuatorBehaviour.getActuatorId() != actuatorId) {
                LOG.warn("Received actuatorBehaviour for unkonwn actuator with id {}",
                        actuatorBehaviour.getActuatorId());
            }
            for (RunningMode runningMode : actuatorBehaviour.getRunningModes()
                    .getDiscreteRunningModeOrContinuousRunningMode()) {
                if (runningMode instanceof StorageDiscreteRunningMode) {
                    for (DiscreteRunningModeElement mode : ((StorageDiscreteRunningMode)runningMode)
                            .getDiscreteRunningModeElement()) {
                        if (mode.getElectricalPower() > 0) {
                            // this is the 'on' mode
                            runningModeOnId = runningMode.getId();
                            runningModes.put(runningModeOnId, runningMode);
                        } else {
                            // this is the 'idle' mode
                            runningModeOffId = runningMode.getId();
                            runningModes.put(runningModeOffId, runningMode);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void measurement(Measurement measurement) {
        getMarket().log(EEventType.DEVICE_STATE,
                "Power consumption: " + measurement.getElectricityMeasurement().getPower() + "W", null);
        LOG.debug("Measurement send to market: {}W", measurement.getElectricityMeasurement().getPower());
    }

    @Override
    public Instruction flexibilityUpdate(StorageUpdate update) {
        StorageInstruction instruction = Efi.build(StorageInstruction.class, getDeviceId());
        ActuatorInstruction actuatorInstruction = new ActuatorInstruction();
        actuatorInstruction.setActuatorId(actuatorId);
        instruction.setActuatorInstructions(new ActuatorInstructions());
        instruction.getActuatorInstructions().getActuatorInstruction().add(actuatorInstruction);

        if (update instanceof StorageStatus) {
            actuatorInstruction.setRunningModeId(currentRoundRunningModeId);
            LOG.info("Intructed mode {} based on market negotiation outcome.",
                    runningModes.get(currentRoundRunningModeId).getLabel());

            // Check if emergency action is needed (market running mode will be overruled in that case):
            fillLevel = ((StorageStatus)update).getCurrentFillLevel();
            int actualRunningModeId = ((StorageStatus)update).getActuatorStatuses()
                    .getActuatorStatus()
                    .get(0)
                    .getCurrentRunningMode();

            if (actualRunningModeId == runningModeOnId &&
                    getRunningModeElement(actualRunningModeId, fillLevel - maxMargin) == null) {
                actuatorInstruction.setRunningModeId(runningModeOffId);
                instruction.setIsEmergencyInstruction(true);
                LOG.warn("Emergency: Temperature ({}) too low while cooling, will switch to idle mode now!", fillLevel);
            }
            if (actualRunningModeId == runningModeOffId &&
                    getRunningModeElement(actualRunningModeId, fillLevel + maxMargin) == null) {
                actuatorInstruction.setRunningModeId(runningModeOnId);
                instruction.setIsEmergencyInstruction(true);
                LOG.warn("Emergency: Temperature ({}) too high while idle, will switch to cooling mode now!",
                        fillLevel);
            }
        }
        return instruction;
    }

    private DiscreteRunningModeElement getRunningModeElement(int runningModeId, double fillLevel) {
        RunningMode runningMode = runningModes.get(runningModeId);
        for (DiscreteRunningModeElement mode : ((StorageDiscreteRunningMode)runningMode)
                .getDiscreteRunningModeElement()) {
            if (fillLevel >= mode.getFillLevelLowerBound() && fillLevel < mode.getFillLevelUpperBound()) {
                return mode;
            }
        }
        LOG.debug("No running element found for runningmode {} at fillLevel {}", runningMode.getLabel(), fillLevel);
        return null;
    }

    private IFritzyApi getMarket() {
        if (cachedFritzyApi == null) {
            cachedFritzyApi = Services.get(IFritzyApiFactory.class).build();
        }
        return cachedFritzyApi;
    }

    /**
     * Call periodically to evaluate market changes
     * 
     */
    public void evaluate() {
        if (getRunningModeElement(runningModeOnId, fillLevel) == null) {
            // cooling not possible (will become too cold), do nothing
            LOG.debug("Temperature ({}) too low, cooling not possible, no market activity", fillLevel);
            return;
        }
        if (nextRoundRunningModeId == runningModeOnId) {
            LOG.debug("Already purchased enough energy, no market activity.");
            return;
        }

        DeviceId deviceId = resourceManager.getDeviceId();
        IFritzyApi market = getMarket();
        EventLogger events = new EventLogger(market);

        // Get balance
        FritzyBalance balance = market.balance();
        events.logBalance(balance);

        // Get max grid capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        events.logLimitActor(deviceCapacity.getGridConnectionLimit());

        // use market price as base for my price
        IAPXPricesApi exxy = Endpoints.get(IAPXPricesApi.class);
        double marketPrice = exxy.getNextQuarterHourPrice().getPrice();

        // TODO WHO: better way to detect which round this is? ==> Expect the round number to become available via the
        // market API.
        Duration remainingTime = Duration.between(Instant.now(), Efi.getNextQuarter());
        int round = 15 - (int)remainingTime.toMinutes();

        // Calculate my price based on fillLevel.
        double offset = marketPriceStartOffset;
        DiscreteRunningModeElement runningModeElement = getRunningModeElement(runningModeOffId, fillLevel);
        // cooling is needed when temperature already too high or when it will become to high next period.
        if (runningModeElement == null || ((currentRoundRunningModeId != runningModeOnId) &&
                (fillLevel + runningModeElement.getFillingRate() * 60 * 15) > runningModeElement
                        .getFillLevelUpperBound())) {
            LOG.debug("Cooling needed, need to buy, increasing myPrice");
            // Cooling is needed, increase price every round (for the last round offset is 0 => accept market price)
            offset = (marketPriceStartOffset / 15) * (15 - round);
        }

        myPrice = marketPrice - offset;
        LOG.debug("myPrice: {} (marketPrice : {}, offset: {}, marketPriceStartOffset {})", myPrice, marketPrice, offset,
                marketPriceStartOffset);

        if (round == 1) {
            // reset needed energy based on running mode power
            neededKWh = getRunningModeElement(runningModeOnId, fillLevel).getElectricalPower() / 1000 * 1 / 4;
            LOG.debug("First round, needed energy set to {} kWh.", neededKWh);
            currentRoundRunningModeId = nextRoundRunningModeId;
            // by default no energy is bought so running mode off.
            nextRoundRunningModeId = runningModeOffId;
        }

        Orders orders = market.orders().getOrders();
        for (Record record : orders.getRecords()) {
            WebOrder order = record.getOrder();
            // my own order?
            if (order.getMakerAddress().equals(market.getAddress())) {
                // when the taker address is set this means someone accepted our order
                if (order.getTakerAddress() != null && !order.getTakerAddress().isEmpty()) {
                    handleEnergyPurchased(order);
                } else {
                    // cancel outstanding orders, new order are created later on based on the new price
                    // TODO WHO: void method, what happens when cancel is impossible? (e.g. when it accepted by another
                    // party during this for loop...)
                    market.cancelOrder(order.getHash());
                    LOG.debug("Order canceled: {}", order);
                }
                continue;
            }
            if (!isInterestingOrder(order, deviceCapacity)) {
                continue;
            }
            OrderReward reward = netty.getOrderReward(market.getAddress(), order.getHash());
            if (!checkAcceptOffer(order, reward)) {
                continue;
            }
            String txId = market.fillOrder(order.getHash());
            market.log(EEventType.ORDER_ACCEPT, order.toString(), null);
            netty.claim(txId, reward.getRewardId());
            market.log(EEventType.REWARD_CLAIM, reward.toString(), null);

            handleEnergyPurchased(order);
        }
        createNewOrder(market);
    }

    /**
     * @param order
     */
    private void handleEnergyPurchased(WebOrder order) {
        neededKWh -= getOfferedKwh(order);
        // TODO WHO: burning the kwh we bought. Is this needed?
        getMarket().burn(BigDecimal.valueOf(getOfferedKwh(order)), EContractAddress.valueOf(order.getMakerAddress()));
        if (neededKWh <= 0) {
            nextRoundRunningModeId = runningModeOnId;
        }

    }

    private void createNewOrder(IFritzyApi market) {
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, BigDecimal.valueOf(myPrice),
                BigDecimal.valueOf(neededKWh));
        String orderDescription = String.format("%f %s for %f %s", myPrice, EContractAddress.EUR, neededKWh,
                EContractAddress.KWH);
        market.log(EEventType.ORDER_OFFER, orderDescription, null);
    }

    private static double getOfferedKwh(WebOrder order) {
        return Double.parseDouble(order.getMakerAssetAmount());
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {
        // check if price is ok
        double priceOffered = Double.parseDouble(order.getTakerAssetAmount());
        if (priceOffered - reward.getReward() > myPrice) {
            LOG.info("Order {} declined because priceOffered ({}) - reward ({}) > myPrice ({})", order, priceOffered,
                    reward, myPrice);
            return false;
        }
        return true;
    }

    private boolean isInterestingOrder(WebOrder order, DeviceCapacity deviceCapacity) {
        // Only interested in buying kWh for EUR:
        if (!(EContractAddress.valueOf(order.getMakerAssetData()) == EContractAddress.KWH &&
                EContractAddress.valueOf(order.getTakerAssetData()) == EContractAddress.EUR)) {
            LOG.info("Order {} declined because it offered {} for {} (instead of kWh for EUR)",
                    order.getMakerAssetData(), order.getTakerAssetData());
            return false;
        }

        // check if offered kWh is what we need (or more)
        double offeredKWh = getOfferedKwh(order);
        if (offeredKWh < neededKWh) {
            LOG.info("Order {} declined because offeredKWh ({}) < neededKWh ({})", order, offeredKWh, neededKWh);
            return false;
        }
        LOG.debug("Order {} is interesingOrder");
        return true;
    }
}
