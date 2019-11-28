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
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

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
import nl.technolution.protocols.efi.util.AbstractCustomerEnergyManager;
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

    private Double fillLevel;
    private Double neededKWh;
    private int actuatorId;

    private double myPrice;

    private Map<Integer, RunningMode> runningModes = new HashMap<>();

    private int runningModeOnId;
    private int runningModeOffId;
    private int currentPeriodRunningModeId;
    private int nextperiodRunningModeId;

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
            throw new IllegalArgumentException("More than 1 actuator configured.");
        }
        actuatorId = flexibilityRegistration.getActuators().getActuator().get(0).getId();
    }

    /**
     * @param storageSystemDescription
     * 
     */
    public void storageSystemDescription(StorageSystemDescription storageSystemDescription) {
        if (!runningModes.isEmpty()) {
            LOG.warn("Received a second storageSystemDescription, will be ignored!");
            return;
        }

        for (ActuatorBehaviour actuatorBehaviour : storageSystemDescription.getActuatorBehaviours()
                .getActuatorBehaviour()) {
            if (actuatorBehaviour.getActuatorId() != actuatorId) {
                LOG.warn("Received actuatorBehaviour for unknown actuator with id {}",
                        actuatorBehaviour.getActuatorId());
                continue;
            }
            for (RunningMode runningMode : actuatorBehaviour.getRunningModes()
                    .getDiscreteRunningModeOrContinuousRunningMode()) {
                processRunningMode(runningMode);
            }
        }

        if (runningModes.isEmpty()) {
            LOG.warn("Received storageSystemDescription did not contain all the information requered.");
        } else {
            // Initialize at 'off' because no energy was purchased for current period
            currentPeriodRunningModeId = runningModeOffId;
            nextperiodRunningModeId = runningModeOffId;
            LOG.info("Received storageSystemDescription with all the information requered.");
        }
    }

    /**
     * @param runningMode
     */
    private void processRunningMode(RunningMode runningMode) {
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

    @Override
    public void measurement(Measurement measurement) {
        try {
            getMarket().log(EEventType.DEVICE_STATE,
                    "Power consumption: " + measurement.getElectricityMeasurement().getPower() + "W", null);
            LOG.debug("Measurement send to market: {}W", measurement.getElectricityMeasurement().getPower());
        } catch (FritzyApiException e) {
            LOG.error("Unable to send Measurement: {}W", measurement.getElectricityMeasurement().getPower(), e);
        }
    }

    @Override
    public Instruction flexibilityUpdate(StorageUpdate update) {
        if (runningModes.isEmpty()) {
            throw new IllegalStateException("No storageSystemDescription recevied yet");
        }
        StorageInstruction instruction = Efi.build(StorageInstruction.class, getDeviceId());
        ActuatorInstruction actuatorInstruction = new ActuatorInstruction();
        actuatorInstruction.setActuatorId(actuatorId);
        instruction.setActuatorInstructions(new ActuatorInstructions());
        instruction.getActuatorInstructions().getActuatorInstruction().add(actuatorInstruction);

        if (update instanceof StorageStatus) {
            actuatorInstruction.setRunningModeId(currentPeriodRunningModeId);
            LOG.info("Intructed mode {} based on market negotiation outcome.",
                    runningModes.get(currentPeriodRunningModeId).getLabel());

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

    @VisibleForTesting
    public Double getFillLevel() {
        return fillLevel;
    }

    /**
     * Call periodically to evaluate market changes
     * 
     * @throws FritzyApiException
     * 
     */
    public void evaluate() throws FritzyApiException {
        if (runningModes.isEmpty()) {
            throw new IllegalStateException("No storageSystemDescription recevied yet");
        }
        if (fillLevel == null) {
            LOG.warn("Fill level unknown, can't evaluate!");
            return;
        }
        DiscreteRunningModeElement runningModeOnElement = getRunningModeElement(runningModeOnId, fillLevel);
        if (runningModeOnElement == null) {
            // cooling not possible (will become too cold), do nothing
            LOG.debug("Temperature ({}) too low, cooling not possible, no market activity", fillLevel);
            return;
        }
        if (nextperiodRunningModeId == runningModeOnId) {
            LOG.debug("Already purchased enough energy, no market activity.");
            return;
        }

        DeviceId deviceId = resourceManager.getDeviceId();
        IFritzyApi market = getMarket();
        EventLogger events = new EventLogger(market);

        // Get max grid capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        events.logLimitActor(deviceCapacity.getGridConnectionLimit());

        // check grit capacity
        double maxAmps = runningModeOnElement.getElectricalPower() / 230d;
        if (maxAmps > deviceCapacity.getGridConnectionLimit()) {
            // not allowed use this much energy
            LOG.warn("Cooling not possible because requered current {} doesn't fit grit capacity {}", maxAmps,
                    deviceCapacity.getGridConnectionLimit());
            return;
        }

        // Get balance
        FritzyBalance balance = market.balance();
        events.logBalance(balance);

        // use market price as base for my price
        IAPXPricesApi exxy = Endpoints.get(IAPXPricesApi.class);
        double marketPrice = exxy.getNextQuarterHourPrice().getPrice();

        int round = detectMarketRound(Clock.systemDefaultZone());

        DiscreteRunningModeElement runningModeOffElement = getRunningModeElement(runningModeOffId, fillLevel);
        DiscreteRunningModeElement currentRunningModeElement = getRunningModeElement(currentPeriodRunningModeId,
                fillLevel);

        myPrice = calclulateMyPrice(
                coolingNeededNextPeriod(fillLevel, currentRunningModeElement, runningModeOffElement,
                        Duration.between(Instant.now(), Efi.getNextQuarter())),
                marketPrice, marketPriceStartOffset, round);

        if (round == 1 || neededKWh == null) {
            // reset needed energy based on running mode power
            neededKWh = runningModeOnElement.getElectricalPower() / 1000 * 1 / 4;
            LOG.debug("First round, needed energy set to {} kWh.", neededKWh);
            currentPeriodRunningModeId = nextperiodRunningModeId;
            // by default no energy is bought so running mode off.
            nextperiodRunningModeId = runningModeOffId;
        }

        Orders orders = market.orders().getOrders();
        for (Record record : orders.getRecords()) {
            WebOrder order = record.getOrder();
            // my own order?
            if (order.getMakerAddress().equals(market.getAddress())) {
                // when the taker address is set this means someone accepted our order
                if (OrderHelper.isAccepted(order)) {
                    handleEnergyPurchased(Double.parseDouble(order.getTakerAssetAmount()));
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

            handleEnergyPurchased(Double.parseDouble(order.getMakerAssetAmount()));
        }
        createNewOrder(market);
    }

    @VisibleForTesting
    static double calclulateMyPrice(boolean coolingNeededNextPeriod, double marketPrice, double marketPriceStartOffset,
            int round) {
        double offset = marketPriceStartOffset;
        double myPrice;

        if (coolingNeededNextPeriod) {
            LOG.debug("Cooling needed, need to buy, increasing myPrice");
            // increase price every round (for the last round offset will be 0 => accept market price)
            offset = (marketPriceStartOffset / (15 - 1)) * (15 - round);
        }

        myPrice = marketPrice - offset;
        LOG.debug("myPrice: {} (marketPrice : {}, marketPriceStartOffset: {}, round: {}, calculated offset: {})",
                myPrice, marketPrice, marketPriceStartOffset, round, offset);
        return myPrice;
    }

    @VisibleForTesting
    static boolean coolingNeededNextPeriod(double fillLevel, DiscreteRunningModeElement currentRunningModeElement,
            DiscreteRunningModeElement offRunningModeElement, Duration remainingPeriod) {
        // already too hot?
        if (offRunningModeElement == null) {
            LOG.debug("Cooling needed: offRunningModeElement == null (already too hot).");
            return true;
        }
        // calculate temperature at end of current period based on current running mode
        double periodEndFillLevel = fillLevel +
                (currentRunningModeElement.getFillingRate() * remainingPeriod.getSeconds());
        // cooling is needed when temperature will be too high at end of next period when not cooling
        double nextPeriodOffFillLevel = periodEndFillLevel + (offRunningModeElement.getFillingRate() * 15 * 60);
        boolean coolingNeeded = (nextPeriodOffFillLevel > offRunningModeElement.getFillLevelUpperBound());
        LOG.debug("Cooling needed: {} (periodEndFillLevel: {} nextPeriodOffFillLevel: {})", coolingNeeded,
                periodEndFillLevel, nextPeriodOffFillLevel);
        return coolingNeeded;
    }

    @VisibleForTesting
    static int detectMarketRound(Clock clock) {
        Duration remainingTime = Duration.between(Instant.now(clock), Efi.getNextQuarter(clock));
        int round = 15 - (int)remainingTime.toMinutes();
        LOG.debug("Market round {} detected based on {}.", round, clock);
        return round;
    }

    /**
     * @param order
     * @throws FritzyApiException
     */
    private void handleEnergyPurchased(double purchasedKWh) throws FritzyApiException {
        neededKWh -= purchasedKWh;
        getMarket().burn(BigDecimal.valueOf(purchasedKWh), EContractAddress.KWH);
        if (neededKWh <= 0) {
            nextperiodRunningModeId = runningModeOnId;
        }
    }

    private void createNewOrder(IFritzyApi market) throws FritzyApiException {
        // price must be for the requested amount of kWh (it is not a 'euro per kwh' price).
        double totalPrice = myPrice * neededKWh;
        if (neededKWh > 0) {
            market.createOrder(EContractAddress.EUR, EContractAddress.KWH, BigDecimal.valueOf(totalPrice),
                    BigDecimal.valueOf(neededKWh));
            String orderDescription = String.format("%f %s for %f %s", totalPrice, EContractAddress.EUR, neededKWh,
                    EContractAddress.KWH);
            market.log(EEventType.ORDER_OFFER, orderDescription, null);
        }
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {
        // check if price is ok
        double priceOffered = Double.parseDouble(order.getTakerAssetAmount());
        // price is for the offered amount of kWh (it is not a 'euro per kwh' price).
        double kWhOffered = Double.parseDouble(order.getMakerAssetAmount());
        double myTotalPrice = myPrice * kWhOffered;

        if (priceOffered - reward.getReward() > myTotalPrice) {
            LOG.info("Order {} declined because priceOffered ({}) - reward ({}) > myTotalPrice ({} ({} * {}))", order,
                    priceOffered, reward.getReward(), myTotalPrice, myPrice, kWhOffered);
            return false;
        }
        return true;
    }

    private boolean isInterestingOrder(WebOrder order, DeviceCapacity deviceCapacity) {
        // Only interested in buying kWh for EUR:
        if (!(order.getMakerAssetData().equals(EContractAddress.KWH.getContractName()) &&
                order.getTakerAssetData().equals(EContractAddress.EUR.getContractName()))) {
            LOG.info("Order {} declined because it offered {} for {} (instead of kWh for EUR)", order,
                    order.getMakerAssetData(), order.getTakerAssetData());
            return false;
        }

        // check if offered kWh is what we need (or more)
        double offeredKWh = Double.parseDouble(order.getMakerAssetAmount());
        if (offeredKWh < neededKWh) {
            LOG.info("Order {} declined because offeredKWh ({}) < neededKWh ({})", order, offeredKWh, neededKWh);
            return false;
        }
        LOG.debug("Order {} is interesing order", order);
        return true;
    }
}
