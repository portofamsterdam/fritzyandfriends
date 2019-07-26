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
import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.core.Log;
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

    private static final double MAX_ORDER_SIZE_KWH = 1;

    private final SunnyResourceManager resourceManager;

    private InflexibleForecast forecast;
    private double marketPriceStartOffset;
    private double availableKWh;

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
        DeviceId deviceId = resourceManager.getDeviceId();
        IFritzyApi market = getMarket();

        // Get balance
        FritzyBalance balance = market.balance();
        market.log(EEventType.BALANCE, balance.getEur().toPlainString(), null);

        // Get max capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        market.log(EEventType.LIMIT_ACTOR, Double.toString(deviceCapacity.getGridConnectionLimit()), null);

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

        if (firstRound) {
            // reset available energy
            availableKWh = getNextQuarterHourForcastedKWh();
            // TOOD WHO: what address to use?
            market.mint(market.getAddress(), BigDecimal.valueOf(availableKWh), EContractAddress.KWH);
            LOG.debug("First round, available energy set to {} kWh based on prediction.", availableKWh);
        }

        Orders orders = market.orders().getOrders();
        for (Record record : orders.getRecords()) {
            WebOrder order = record.getOrder();
            // my own order?
            if (order.getMakerAddress().equals(market.getAddress())) {
                // when the taker address is set this means someone accepted our order
                if (!order.getTakerAddress().isEmpty()) {
                    // energy sold so no longer available:
                    availableKWh -= getRequestedKwh(order);
                } else {
                    // cancel outstanding orders, new order are created later on based on the new price
                    // TODO WHO: void method, what happens when cancel is impossible? (e.g. when it accepted by another
                    // party during this for loop...)
                    market.cancelOrder(order.getHash());
                    LOG.debug("Order canceled: " + order);
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

            // energy sold so no longer available:
            availableKWh -= getRequestedKwh(order);
        }
        createNewOrders(market);
    }

    private void createNewOrders(IFritzyApi market) {
        double totalOrderKwh = availableKWh;
        while (totalOrderKwh > 0) {
            double orderSize;
            if (totalOrderKwh > MAX_ORDER_SIZE_KWH) {
                orderSize = MAX_ORDER_SIZE_KWH;
            } else {
                orderSize = totalOrderKwh;
            }

            market.createOrder(EContractAddress.KWH, EContractAddress.EUR, BigDecimal.valueOf(orderSize),
                    BigDecimal.valueOf(myPrice));
            String orderDescription = String.format("%f %s for %f %s", orderSize, EContractAddress.KWH, myPrice,
                    EContractAddress.EUR);
            market.log(EEventType.ORDER_OFFER, orderDescription, null);

            totalOrderKwh -= orderSize;
        }
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
        double priceOffered = Double.parseDouble(order.getTakerAssetAmount());
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
        getMarket().log(EEventType.DEVICE_STATE,
                "Generating power: " + measurement.getElectricityMeasurement().getPower() + "W", null);
    }
}
