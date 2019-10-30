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
package nl.technolution.sunny.marketnegotiator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;

import io.dropwizard.jersey.params.InstantParam;
import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.apis.exxy.ApxPrice;
import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.ApiEvent;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.pvcast.cache.IPvForecastsCacher;
import nl.technolution.sunny.pvcast.cache.PvCastClientStub;
import nl.technolution.sunny.pvcast.cache.PvForecastCache;
import nl.technolution.sunny.pvcast.client.IPvCastClient;
import nl.technolution.sunny.solaredge.ISESessionFactory;
import nl.technolution.sunny.solaredge.SESessionFactory;
import nl.technolution.sunny.trader.SunnyNegotiator;
import nl.technolution.sunny.trader.SunnyResourceHelper;
import nl.technolution.sunny.trader.SunnyResourceManager;

/**
 * Test negotiator
 */
public class NegotiatorTest {

    private static final String PASSWORD = "";
    private static final String SUNNY = "SUNNY_TEST";
    private static final DeviceId DEVICE_ID = new DeviceId(SUNNY);
    private SunnyNegotiator sn;
    private NettyApiImpl netty;

    private SunnyConfig config;

    private PvCastClientStub pvCastClientStub;
    private SunnyResourceHelper resourceHelper;

    @Before
    public void setup() {
        // Set locale so the string compares with balance don't fail on decimal seperator differences...
        Locale.setDefault(Locale.US);
        config = new SunnyConfig();
        config.setUseSolarEdgeStub(true);
        config.setMarketPriceStartOffset(0.10);
        config.setDeviceId(DEVICE_ID.getDeviceId());
        config.setMarket(new MarketConfig(true, "", "", ""));

        SunnyResourceManager resourceManager = new SunnyResourceManager(DEVICE_ID);

        FritzyApiStub market = FritzyApiStub.instance();
        FritzyApiFactory service = new FritzyApiFactory();
        MarketConfig marketConfig = new MarketConfig(true, PASSWORD, DEVICE_ID.getDeviceId(), PASSWORD);
        config.setMarket(marketConfig);
        service.init(config);
        Services.put(IFritzyApiFactory.class, service);

        pvCastClientStub = new PvCastClientStub();
        pvCastClientStub.init(config);
        Services.put(IPvCastClient.class, pvCastClientStub);

        pvCastClientStub.setForcastedPower(1000);

        PvForecastCache pvForecastCache = new PvForecastCache();
        pvForecastCache.init(config);
        Services.put(IPvForecastsCacher.class, pvForecastCache);

        SESessionFactory seSessionFactory = new SESessionFactory();
        seSessionFactory.init(config);
        Services.put(ISESessionFactory.class, seSessionFactory);

        netty = new NettyApiImpl();
        Endpoints.put(INettyApi.class, netty);
        Endpoints.put(IAPXPricesApi.class, new APXPricesApiStub());

        FritzyApiStub.reset();

        market.register(SUNNY, SUNNY, PASSWORD);
        sn = new SunnyNegotiator(config, resourceManager);
        resourceManager.registerCustomerEnergyManager(sn);

        resourceHelper = new SunnyResourceHelper(DEVICE_ID);
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void noFrocast() throws FritzyApiException {
        expectedEx.expect(IllegalStateException.class);
        // Evaluate the market
        sn.evaluate();
    }

    @Test
    public void emptyMarket() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();
        BigDecimal mintedEur = BigDecimal.valueOf(10);
        market.login(SUNNY, PASSWORD);
        market.mint(market.getAddress(), mintedEur, EContractAddress.EUR);

        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        // Evaluate the market
        sn.evaluate();

        // check balance
        ApiEvent balanceEvent = market.getFirstEventOfType(EEventType.BALANCE);
        assertEquals("Balance KWH: 0.00, EUR: 10.00", balanceEvent.getMsg());

        // check capacity
        ApiEvent limitActorEvent = market.getFirstEventOfType(EEventType.LIMIT_ACTOR);
        assertEquals("Limit actor update limit=64.00", limitActorEvent.getMsg());

        // check created orders
        assertEquals(1, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void createOrderInEmptyMarket() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();

        market.login(SUNNY, PASSWORD);
        netty.rewardToGive = 2;

        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        sn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
    }

    @Test
    public void createMultipleOrderInEmptyMarket() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();

        market.login(SUNNY, PASSWORD);
        netty.rewardToGive = 2;

        pvCastClientStub.setForcastedPower(3 * SunnyNegotiator.MAX_ORDER_SIZE_KWH * 1000 * 4);
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        sn.evaluate();
        // expect 3 orders
        assertEquals(3, market.orders().getOrders().getRecords().length);

        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
    }

    @Test
    public void acceptExistingOrder() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();

        pvCastClientStub.setForcastedPower(125 * 4);
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        String batty = "batty";
        market.register(batty, batty, batty);
        market.login(batty, batty);
        BigDecimal eur = BigDecimal.valueOf(0.01);
        BigDecimal kWh = BigDecimal.valueOf(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(SUNNY, PASSWORD);
        netty.rewardToGive = 2;
        sn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertTrue(netty.claimed);

        // order accepted by sunny
        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
    }

    @Test
    public void acceptExistingOrders() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();

        pvCastClientStub.setForcastedPower(1000 * 4);
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        String batty = "batty";
        market.register(batty, batty, batty);
        market.login(batty, batty);
        BigDecimal eur = BigDecimal.valueOf(0.05);
        BigDecimal kWh = BigDecimal.valueOf(0.500d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        eur = BigDecimal.valueOf(0.03);
        kWh = BigDecimal.valueOf(0.300d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        eur = BigDecimal.valueOf(0.03);
        kWh = BigDecimal.valueOf(0.300d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(SUNNY, PASSWORD);
        netty.rewardToGive = 2;
        sn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertTrue(netty.claimed);

        // 3 existing orders + 1 new
        assertEquals(4, market.orders().getOrders().getRecords().length);
        // 2 orders accepted by sunny
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[1].getOrder().getTakerAddress());
        // 3th order not excepted by sunny
        assertEquals(null, market.orders().getOrders().getRecords()[2].getOrder().getTakerAddress());
        // 1 new order by sunny for remaining energy (1 - 0.5 - 0.3 = 0.2)
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[3].getOrder().getMakerAddress());
        assertEquals("0.2", market.orders().getOrders().getRecords()[3].getOrder().getMakerAssetAmount());
    }

    @Test
    public void notEnoughEnergyforOrder() throws FritzyApiException {
        int whThisQuarter = 1000;
        FritzyApiStub market = FritzyApiStub.instance();

        pvCastClientStub.setForcastedPower(whThisQuarter * 4);
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        String batty = "batty";
        market.register(batty, batty, batty);
        market.login(batty, batty);
        BigDecimal eur = BigDecimal.valueOf(1);
        BigDecimal kWh = BigDecimal.valueOf(whThisQuarter * 1.1 / 1000);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(SUNNY, PASSWORD);
        netty.rewardToGive = 2;
        sn.evaluate();
        assertFalse(netty.orderRewardRequested);
        assertFalse(netty.claimed);

        // order not accepted by sunny, new order made
        assertEquals(2, market.orders().getOrders().getRecords().length);
        // 1st orders not accepted by sunny
        assertEquals(null, market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
        // 1 new order by sunny for remaining energy
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[1].getOrder().getMakerAddress());
        assertEquals(Double.toString(whThisQuarter / 1000),
                market.orders().getOrders().getRecords()[1].getOrder().getMakerAssetAmount());
    }

    @Test
    public void acceptExistingOrderNoReward() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();

        pvCastClientStub.setForcastedPower(125 * 4);
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        String batty = "batty";
        market.register(batty, batty, batty);
        market.login(batty, batty);
        BigDecimal eur = BigDecimal.valueOf(0.04);
        BigDecimal kWh = BigDecimal.valueOf(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(SUNNY, PASSWORD);
        netty.rewardToGive = 0;
        sn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertTrue(netty.claimed);

        // order accepted by sunny
        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
    }

    @Test
    public void NotAcceptOrderPriceTooLow() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();

        pvCastClientStub.setForcastedPower(125 * 4);
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        String batty = "batty";
        market.register(batty, batty, batty);
        market.login(batty, batty);
        BigDecimal eur = BigDecimal.valueOf(0.01);
        BigDecimal kWh = BigDecimal.valueOf(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(SUNNY, PASSWORD);
        netty.rewardToGive = 0;
        sn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertFalse(netty.claimed);

        // order not accepted by sunny, own order created
        assertEquals(2, market.orders().getOrders().getRecords().length);
        assertEquals(null, market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[1].getOrder().getMakerAddress());
    }

    @Test
    public void cancelExistingOrders() throws FritzyApiException {
        int whThisQuarter = 1000;
        FritzyApiStub market = FritzyApiStub.instance();

        pvCastClientStub.setForcastedPower(whThisQuarter * 4);
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        // Create order by sunny, market is empty
        netty.rewardToGive = 1;
        sn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        // Create order by batty for sunny to accept
        String batty = "batty";
        market.register(batty, batty, batty);
        market.login(batty, batty);
        BigDecimal eur = BigDecimal.valueOf(1);
        BigDecimal kWh = BigDecimal.valueOf(whThisQuarter / 1000);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        // Sunny accepts the order from sunny
        market.login(SUNNY, PASSWORD);

        sn.evaluate();
        assertTrue(netty.claimed);
        assertTrue(netty.orderRewardRequested);
        // Existing orders by sunny are gone, there is only 1 order left (the batty order that is accepted by sunny)
        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

    }

    @Test
    public void noNewOrdersBattyAcceptedSunny() throws FritzyApiException {
        int whThisQuarter = 500;
        FritzyApiStub market = FritzyApiStub.instance();

        pvCastClientStub.setForcastedPower(whThisQuarter * 4);
        String batty = "batty";
        market.register(batty, batty, PASSWORD);

        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        sn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        WebOrder order = market.orders().getOrders().getRecords()[0].getOrder();
        market.login(batty, batty);
        String sunnyAddr = market.getAddress();
        market.mint(market.getAddress(), new BigDecimal(order.getTakerAssetAmount()),
                EContractAddress.getByContractName(order.getTakerAssetData()));
        market.fillOrder(order.getHash());

        market.login(SUNNY, PASSWORD);
        sn.evaluate();

        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getMakerAddress());
        assertEquals(sunnyAddr, market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);

        sn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);
        // no new orders created, there is only 1 order left (the batty order that is accepted by sunny)
        market.login(batty, batty);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

    }

    @Test
    public void sellKwhOrder() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();
        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());

        String batty = "batty";
        market.register(batty, batty, batty);
        market.login(batty, PASSWORD);
        BigDecimal eur = BigDecimal.valueOf(1);
        BigDecimal kWh = BigDecimal.valueOf(1);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        netty.rewardToGive = 2;
        sn.evaluate();
        assertFalse(netty.orderRewardRequested);
        assertFalse(netty.claimed);

        // Expect 2 unaccepted orders to be left (one batty, one sunny)
        assertEquals(2, market.orders().getOrders().getRecords().length);
        assertNull(market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
        assertNull(market.orders().getOrders().getRecords()[1].getOrder().getTakerAddress());
    }

    @Test
    public void noOrderDueToGridLimit() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();

        // set power higher than grid limit
        pvCastClientStub.setForcastedPower((int)(netty.getCapacity(SUNNY).getGridConnectionLimit() * 230) + 1);

        sn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        sn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
        // Expect no orders
        assertEquals(0, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void orderCreation() throws FritzyApiException {
        double availableKWh = (SunnyNegotiator.MAX_ORDER_SIZE_KWH * 9) + 1;
        double myPrice = 0.12;
        FritzyApiStub market = FritzyApiStub.instance();

        market.login(SUNNY, PASSWORD);
        market.mint(market.getAddress(), BigDecimal.valueOf(availableKWh), EContractAddress.KWH);

        SunnyNegotiator.createNewOrders(market, availableKWh, myPrice);

        assertEquals(10, market.orders().getOrders().getRecords().length);
    }

    private static class APXPricesApiStub implements IAPXPricesApi {
        @Override
        public ApxPrice getCurrentPrice() {
            return new ApxPrice(0.21d);
        }

        @Override
        public ApxPrice getNextQuarterHourPrice() {
            return new ApxPrice(0.21d);
        }

        @Override
        public ApxPrice getPrice(InstantParam requestedDateTime) {
            return new ApxPrice(0.21d);
        }
    }

    private static class NettyApiImpl implements INettyApi {

        private static final Logger LOG = Log.getLogger();
        private boolean claimed = false;
        private boolean orderRewardRequested = false;
        private double gridCapacity = 64d;
        private double groupCapacity = 64d;
        private double rewardToGive = 0d;

        @Override
        public DeviceCapacity getCapacity(String deviceId) {
            LOG.debug("getCapacity for {} returned {} {}", deviceId, gridCapacity, groupCapacity);
            return new DeviceCapacity(gridCapacity, groupCapacity);
        }

        @Override
        public void claim(String txHash, String rewardId) {
            LOG.debug("claim by {} id {}", txHash, rewardId);
            claimed = true;
        }

        @Override
        public OrderReward getOrderReward(String taker, String orderHash) {
            LOG.debug("getOrderReward by {} returns {}", taker, rewardToGive);
            orderRewardRequested = true;
            OrderReward reward = new OrderReward();
            reward.setClaimTaker(taker);
            reward.setExpireTs(LocalDateTime.MAX);
            reward.setOrderHash("0x00");
            reward.setReward(rewardToGive);
            reward.setRewardId("1");
            return reward;
        }
    }
}
