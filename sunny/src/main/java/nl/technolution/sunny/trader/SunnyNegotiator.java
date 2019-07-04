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
package nl.technolution.sunny.trader;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.core.Log;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dashboard.IEvent;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApi;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.ElectricityProfile.Element;
import nl.technolution.protocols.efi.InflexibleForecast;
import nl.technolution.protocols.efi.InflexibleRegistration;
import nl.technolution.protocols.efi.InflexibleUpdate;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.util.Efi;
import nl.technolution.protocols.efi.util.XmlUtils;
import nl.technolution.sunny.app.SunnyConfig;

/**
 * 
 */
public class SunnyNegotiator extends AbstractCustomerEnergyManager<InflexibleRegistration, InflexibleUpdate> {
    private static final Logger LOG = Log.getLogger();

    private final FritzyApi market;
    private final SunnyResourceManager resourceManager;

    private InflexibleForecast forecast;
    private double marketPriceStartOffset;
    private double availableKWh;

    private double myPrice;

    /**
     *
     * @param config config used for trading
     * @param resourceManager to control devices
     */
    public SunnyNegotiator(SunnyConfig config, SunnyResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        market = new FritzyApi(config.getMarket().getMarketUrl());
        // TODO: fix config
        // market.login(config.getMarket().getEmail(), config.getMarket().getPassword());
        marketPriceStartOffset = config.getMarketPriceStartOffset();
    }

    /**
     * Call periodicly to evaluate market changes
     */
    public void evaluate() {
        IEvent events = Services.get(IEvent.class);
        DeviceId deviceId = resourceManager.getDeviceId();

        // Get balance
        BigDecimal balance = market.balance();
        events.log(EEventType.BALANCE, balance.toPlainString(), null);

        // Get max capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        events.log(EEventType.LIMIT_ACTOR, Double.toString(deviceCapacity.getGridConnectionLimit()), null);

        // use market price as base for my price
        // TODO WHO: enable this once api is moved to 'common' and add it as api to config in sunny_xx.json
        // IExxyApi exxy = Endpoints.get(IExxyApi.class);
        // double marketPrice = exxy.getNextQuarterHourPrice().getPrice();
        myPrice = 0.21;

        Duration remainingTime = Duration.between(Instant.now(), Efi.getNextQuarter());
        boolean lastRound = remainingTime.getSeconds() < 61;
        boolean firstRound = remainingTime.getSeconds() > 14 * 60 + 1;

        // calculate my price based on remaining time (for the last round accept market price)
        if (!lastRound) {
            myPrice += (marketPriceStartOffset / 15) * remainingTime.toMinutes();
        }

        if (firstRound) {
            // reset available energy
            availableKWh = getNextQuarterHourForcastedKWh();
        }

        Orders orders = market.orders().getOrders();
        for (WebOrder order : orders.getRecords()) {
            if (!isInterestingOrder(order, deviceCapacity)) {
                continue;
            }
            OrderReward reward = netty.getOrderReward(order.getHash());
            if (!checkAcceptOffer(order, reward)) {
                continue;
            }

            String txId = market.fillOrder(order.getHash());
            // TODO WHO: log order as 'data' instead of event (order is not IJsonnable at the moment...)
            events.log(EEventType.ORDER_ACCEPT, order.toString(), null);
            netty.claim(txId, reward.getRewardId());
            // TODO WHO: log reward as 'data' instead of event (order is not IJsonnable at the moment...)
            events.log(EEventType.REWARD_CLAIM, reward.toString(), null);

            // energy sold so no longer available:
            availableKWh -= getRequestedKwh(order);
        }
        // TODO WHO: post oder for remaining production?? MArtin is working on this....
        // NOTE: when we accepted one or more proposals we should cancel (some) of our own proposals because the amount
        // of available energy has changed.
    }

    private boolean isInterestingOrder(WebOrder order, DeviceCapacity deviceCapacity) {
        // Only interested in selling kWh for EUR:
        if (!(order.getMakerAssetData().equalsIgnoreCase("EUR") && order.getTakerAssetData().equalsIgnoreCase("kWh"))) {
            LOG.info("Order {} declined because it offered {} for {} (instead of EUR for kWh)",
                    order.getMakerAssetData(), order.getTakerAssetData());
            return false;
        }

        // check if requested Wh can be met
        double requestedKWh = getRequestedKwh(order);
        if (requestedKWh > availableKWh) {
            LOG.info("Order {} declined because requestedKWh ({})> availableKWh ({})", order, requestedKWh,
                    availableKWh);
            return false;
        }
        LOG.debug("Order {} is interesingOrder");
        return true;
    }

    private static double getRequestedKwh(WebOrder order) {
        // TODO WHO: is this the right field?
        return Double.parseDouble(order.getMakerAssetAmount());
    }

    /**
     * @return
     */
    private double getNextQuarterHourForcastedKWh() {
        Instant nextQuarter = Efi.getNextQuarter();
        Instant start = forecast.getValidFrom().toGregorianCalendar().toInstant();
        for (Element e : forecast.getForecastProfiles().getElectricityProfile().getElement()) {
            Duration duration = XmlUtils.fromXmlDuration(e.getDuration());
            if (start.plus(duration).isAfter(nextQuarter)) {
                return e.getPower() * 0.25 / 1000; // W to kWh/quarter hour;
            }
        }
        throw new Error("No forcasted power info available for next quarter (" + nextQuarter + ")");
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {
        // check if price is ok
        double priceOffered = Double.parseDouble(order.getTakerAssetAmount()); // TODO WHO: is this the right field?
        if (priceOffered + reward.getReward() < myPrice) {
            LOG.info("Order {} declined because priceOffered ({}) + reward ({}) < myPrice ({})", order, priceOffered,
                    reward, myPrice);
            return false;
        }
        return true;
    }

    @Override
    public Instruction flexibilityUpdate(InflexibleUpdate update) {
        if (update instanceof InflexibleForecast) {
            forecast = (InflexibleForecast)update;
        }
        // Curtailment is not possible so instruction is always empty
        return Efi.build(StorageInstruction.class, getDeviceId());
    }

    @Override
    public void measurement(Measurement measurement) {
        Services.get(IEvent.class)
                .log(EEventType.DEVICE_STATE,
                        "Generating power: " + measurement.getElectricityMeasurement().getPower() + "W", null);
    }
}
