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
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.EventLogger;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;
import nl.technolution.protocols.efi.ElectricityProfile.Element;
import nl.technolution.protocols.efi.InflexibleForecast;
import nl.technolution.protocols.efi.InflexibleRegistration;
import nl.technolution.protocols.efi.InflexibleUpdate;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.util.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.util.Efi;
import nl.technolution.protocols.efi.util.XmlUtils;
import nl.technolution.sunny.app.SunnyConfig;

/**
 * 
 */
public class SunnyNegotiator extends AbstractCustomerEnergyManager<InflexibleRegistration, InflexibleUpdate> {
    public static final int MAX_ORDER_SIZE_KWH = 1;

    private static final Logger LOG = Log.getLogger();

    private final SunnyResourceManager resourceManager;

    private InflexibleForecast forecast;
    private double marketPriceStartOffset;
    private Double availableKWh = null;

    private double myPrice;

    private IFritzyApi cachedFritzyApi;

    /**
     *
     * @param config config used for trading
     * @param resourceManager to control devices
     */
    public SunnyNegotiator(SunnyConfig config, SunnyResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        marketPriceStartOffset = config.getMarketPriceStartOffset();
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
        if (forecast == null) {
            throw new IllegalStateException("No forecast available yet");
        }
        DeviceId deviceId = resourceManager.getDeviceId();
        IFritzyApi market = getMarket();
        EventLogger events = new EventLogger(market);

        // Get balance
        FritzyBalance balance = market.balance();
        events.logBalance(balance);

        // Get max capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        events.logLimitActor(deviceCapacity.getGridConnectionLimit());

        // use market price as base for my price
        IAPXPricesApi exxy = Endpoints.get(IAPXPricesApi.class);
        double marketPrice = exxy.getNextQuarterHourPrice().getPrice();

        // TODO WHO: better way to detect first round? ==> Maybe the round number will become available via the market
        // API.
        Duration remainingTime = Duration.between(Instant.now(), Efi.getNextQuarter());
        boolean firstRound = remainingTime.getSeconds() > 14 * 60;

        // calculate my price based on remaining time (for the last round remaining minutes is 0 so offset is 0 =>
        // accept market price)
        double offset = (marketPriceStartOffset / 15) * remainingTime.toMinutes();
        myPrice = marketPrice + offset;
        LOG.debug("myPrice: {} (marketPrice : {}, offset: {}, marketPriceStartOffset {})", myPrice, marketPrice, offset,
                marketPriceStartOffset);

        if (firstRound || availableKWh == null) {
            // reset available energy
            availableKWh = getNextQuarterHourForcastedKWh(forecast);
            market.mint(market.getAddress(), BigDecimal.valueOf(availableKWh), EContractAddress.KWH);
            LOG.debug("First round, available energy set to {} kWh based on prediction.", availableKWh);

            // check grit capacity (kWh/quarter hour * 4 = kWh/hour = kW => kW * 1000 = W => W / V (230) = A
            double maxAmps = availableKWh * 4 * 1000 / 230d;
            if (maxAmps > deviceCapacity.getGridConnectionLimit()) {
                // not allowed use this much energy
                LOG.warn(
                        "Available energy results in {}A which doesn't fit grit capacity {}A, curtailment " +
                                "not supported by Sunny so nothing we can do on this...",
                        maxAmps, deviceCapacity.getGridConnectionLimit());
                return;
            }
        }

        Orders orders = market.orders().getOrders();
        for (Record record : orders.getRecords()) {
            WebOrder order = record.getOrder();
            // my own order?
            if (order.getMakerAddress().equals(market.getAddress())) {
                // when the taker address is set this means someone accepted our order
                if (order.getTakerAddress() != null && !order.getTakerAddress().isEmpty()) {
                    // energy sold so no longer available:
                    availableKWh -= Double.parseDouble(order.getMakerAssetAmount());
                } else {
                    // cancel outstanding orders, new order are created later on based on the new price
                    market.cancelOrder(order.getHash());
                    LOG.debug("Order canceled: {}", order);
                }
                continue;
            }
            if (!isInterestingOrder(order)) {
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

            // energy sold so no longer available:
            availableKWh -= Double.parseDouble(order.getTakerAssetAmount());
        }
        createNewOrders(market, availableKWh, myPrice);
    }

    @VisibleForTesting
    public static void createNewOrders(IFritzyApi market, double availableKWh, double myPrice) {
        double totalOrderKwh = availableKWh;
        while (totalOrderKwh > 0) {
            double orderSize;
            if (totalOrderKwh > MAX_ORDER_SIZE_KWH) {
                orderSize = MAX_ORDER_SIZE_KWH;
            } else {
                orderSize = totalOrderKwh;
            }
            // price must be for the requested amount of kWh (it is not a 'euro per kwh' price).
            double totalPrice = myPrice * orderSize;

            market.createOrder(EContractAddress.KWH, EContractAddress.EUR, BigDecimal.valueOf(orderSize),
                    BigDecimal.valueOf(totalPrice));
            String orderDescription = String.format("%f %s for %f %s", orderSize, EContractAddress.KWH, totalPrice,
                    EContractAddress.EUR);
            market.log(EEventType.ORDER_OFFER, orderDescription, null);

            totalOrderKwh -= orderSize;
        }
    }

    private boolean isInterestingOrder(WebOrder order) {
        // Only interested in selling kWh for EUR:
        if (!(order.getMakerAssetData().equalsIgnoreCase("EUR") && order.getTakerAssetData().equalsIgnoreCase("kWh"))) {
            LOG.info("Order {} declined because it offered {} for {} (instead of EUR for kWh)", order,
                    order.getMakerAssetData(), order.getTakerAssetData());
            return false;
        }

        // check if requested Wh can be met
        double requestedKWh = Double.parseDouble(order.getTakerAssetAmount());
        if (requestedKWh > availableKWh) {
            LOG.info("Order {} declined because requestedKWh ({})> availableKWh ({})", order, requestedKWh,
                    availableKWh);
            return false;
        }
        LOG.debug("Order {} is interesingOrder", order);
        return true;
    }

    /**
     * @return
     */
    private static double getNextQuarterHourForcastedKWh(InflexibleForecast forecast) {
        Instant nextQuarter = Efi.getNextQuarter();
        Instant start = forecast.getValidFrom().toGregorianCalendar().toInstant();
        for (Element e : forecast.getForecastProfiles().getElectricityProfile().getElement()) {
            Duration duration = XmlUtils.fromXmlDuration(e.getDuration());
            if (start.plus(duration).isAfter(nextQuarter)) {
                // convert power in W to kWh/quarter hour
                return e.getPower() / 1000 * 0.25;
            }
        }
        throw new IllegalStateException("No forcasted power info available for next quarter (" + nextQuarter + ")");
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {
        // check if price is ok
        double priceOffered = Double.parseDouble(order.getMakerAssetAmount());
        // price is for the offered amount of kWh (it is not a 'euro per kwh' price).
        double kWhAsked = Double.parseDouble(order.getTakerAssetAmount());
        double myTotalPrice = myPrice * kWhAsked;

        if (priceOffered + reward.getReward() < myTotalPrice) {
            LOG.info("Order {} declined because priceOffered ({}) + reward ({}) < myTotalPrice ({} ({} * {}))", order,
                    priceOffered, reward.getReward(), myTotalPrice, myPrice, kWhAsked);
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
}
