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

    /**
     *
     * @param config config used for trading
     * @param resourceManager to control devices
     */
    public SunnyNegotiator(SunnyConfig config, SunnyResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        market = new FritzyApi(config.getMarket().getMarketUrl());
        market.login(config.getMarket().getEmail(), config.getMarket().getPassword());
        marketPriceStartOffset = config.getMarketPriceStartOffset();
    }

    /**
     * Call periodicly to evaluate market changes
     */
    public void evaluate() {
        IEvent events = Services.get(IEvent.class);

        // Get balance
        BigDecimal balance = market.balance();
        events.log(EEventType.BALANCE, balance.toPlainString(), null);

        // Get max capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceId deviceId = resourceManager.getDeviceId(); // TODO MKE get deviceId here
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        events.log(EEventType.LIMIT_ACTOR, Double.toString(deviceCapacity.getGridConnectionLimit()), null);

        Orders orders = market.orders().getOrders();
        for (WebOrder order : orders.getRecords()) {
            if (!isInterestingOrder(order, balance, deviceCapacity)) {
                continue;
            }
            OrderReward reward = netty.getOrderReward(order.getHash());
            if (!checkAcceptOffer(order, reward)) {
                continue;
            }

            String txId = market.fillOrder(order.getHash());
            netty.claim(txId, reward.getRewardId());
        }
        // TODO WHO: post oder for remaining production?? MArtin is working on this....
    }

    private boolean isInterestingOrder(WebOrder order, BigDecimal balance, DeviceCapacity deviceCapacity) {
        // TODO WHO: SEE API documentation: http://82.196.13.251/api/docs/#/Order/post_me_order
        // 1) check if requested Wh can be met (based on forecast)
        double power = getNextQuarterHourForcastedPower();
        double availableWh = power * 0.25; // W to Wh/quarter hour
        double requestedWh = Double.parseDouble(order.getMakerAssetAmount()); // TODO WHO: is this the right field and
                                                                              // in Wh?
        if (requestedWh > availableWh) {
            LOG.info("Order {} declined because requestedWh ({})> availableWh ({})", order, requestedWh, availableWh);
            return false;
        }
        // 2) check if price is ok
        double myPrice = 0.21; // TODO WHO: get price from Exxy, see
                               // nl.technolution.batty.trader.BatteryNegotiator.evaluate()
        // 2a) calc my price based on remaining time
        Duration remainingTime = Duration.between(Instant.now(), Efi.getNextQuarter());
        boolean lastRound = remainingTime.getSeconds() > 61;
        if (!lastRound) {
            myPrice += (marketPriceStartOffset / 15) * remainingTime.getSeconds();
        }
        double priceOffered = Double.parseDouble(order.getMakerFee()); // TODO WHO: is this the right field?
        if (priceOffered < myPrice) {
            LOG.info("Order {} declined because priceOffered ({}) < myPrice ({})", order, priceOffered, myPrice);
            return false;
        }
        // TODO WHO is GridConnectionLimit in A?
        // TODO WHO: move definiton of voltage to some common place
        if (lastRound && deviceCapacity.getGridConnectionLimit() < power / 230) {
            // TODO WHO: deviceCapacity.getGridConnectionLimit is de limiet van de 'lokale' aansluiting (dus tussen
            // sunny en de rest van de systemen, kan sunny niets aan doen.
            // Altijd loggen via events.log (zie batty).
            // TODO WHO: can't deliver to grid so should accept order from any other when in last bidding round???
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    private double getNextQuarterHourForcastedPower() {
        Instant nextQuarter = Efi.getNextQuarter();
        Instant start = forecast.getValidFrom().toGregorianCalendar().toInstant();
        for (Element e : forecast.getForecastProfiles().getElectricityProfile().getElement()) {
            Duration duration = XmlUtils.fromXmlDuration(e.getDuration());
            if (start.plus(duration).isAfter(nextQuarter)) {
                return e.getPower();
            }
        }
        throw new Error("No forcasted power info available for next quarter (" + nextQuarter + ")");
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {
        // TODO WHO: What is this supposed to do?
        // Verschil met 'isInterestingOrder' is dat hier de 'rewards bij in zitten. Dit zijn euro's die de gene die de
        // order accepteerd krijgt.
        return false;
    }

    @Override
    public Instruction flexibilityUpdate(InflexibleUpdate update) {
        if (update instanceof InflexibleForecast) {
            forecast = (InflexibleForecast)update;
        }
        // TODO WHO: returning this empty shell seems not very useful and it is never used?? ask Martin...

        // Can be empty for sunny...
        return Efi.build(StorageInstruction.class, getDeviceId());
    }
}
