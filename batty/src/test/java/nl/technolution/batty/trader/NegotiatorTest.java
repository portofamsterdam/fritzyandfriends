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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import io.dropwizard.jersey.params.InstantParam;
import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.apis.exxy.ApxPrice;
import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.batty.xstorage.cache.MachineDataCache;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageStub;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.ApiEvent;
import nl.technolution.fritzy.wallet.model.EContractAddress;

/**
 * Test negotiator
 */
public class NegotiatorTest {

    private static final String PASSWORD = "";
    private static final String BATTY = "batty";
    private static final DeviceId DEVICE_ID = new DeviceId(BATTY);
    private BatteryNegotiator bn;
    private NettyApiImpl netty;
    
    @Before
    public void setup() {

        BattyResourceManager resourceManager = new BattyResourceManager(DEVICE_ID);
        BattyConfig config = new BattyConfig();
        config.setBuyMargin(1);
        config.setSellMargin(1);

        FritzyApiStub market = FritzyApiStub.instance();
        FritzyApiFactory service = new FritzyApiFactory();
        MarketConfig marketConfig = new MarketConfig(true, PASSWORD, DEVICE_ID.getDeviceId(), PASSWORD);
        config.setMarket(marketConfig);
        service.init(config);
        Services.put(IFritzyApiFactory.class, service);

        XStorageFactory xStorage = new XStorageFactory();
        config.setUseStub(true);
        xStorage.init(config);
        Services.put(IXStorageFactory.class, xStorage);
        Services.put(IMachineDataCacher.class, new MachineDataCache());

        netty = new NettyApiImpl();
        Endpoints.put(INettyApi.class, netty);
        Endpoints.put(IAPXPricesApi.class, new APXPricesApiStub());

        FritzyApiStub.reset();
        XStorageStub.reset();

        market.register(BATTY, BATTY, PASSWORD);
        bn = new BatteryNegotiator(resourceManager, config);
    }

    @Test
    public void emptyMarket() {

        FritzyApiStub market = FritzyApiStub.instance();
        BigDecimal mintedEur = new BigDecimal(10);
        market.mint(market.getAddress(), mintedEur, EContractAddress.EUR);

        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        // Evaluate the market
        bn.evaluate();
        assertTrue(market.getAllEvents().isEmpty()); // fillLevel unknown, it doesn't do anything
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.evaluate();
        assertTrue(market.getAllEvents().isEmpty()); // system description unknown, it doesn't do anything
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());
        bn.evaluate();

        // check balance
        ApiEvent balanceEvent = market.getFirstEventOfType(EEventType.BALANCE);
        assertEquals(mintedEur.toPlainString(), balanceEvent.getMsg());

        // check capacity
        ApiEvent limitActorEvent = market.getFirstEventOfType(EEventType.LIMIT_ACTOR);
        assertEquals(Double.toString(netty.getCapacity(BATTY).getGridConnectionLimit()),
                limitActorEvent.getMsg());

        // check created orders
        assertEquals(2, market.orders().getOrders().getRecords().length);
    }

    @Test 
    public void acceptExistingOrderCharge() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());
        
        
        String sunny = "sunny";
        market.register(sunny , sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(1);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.mint(market.getAddress(), kWh, EContractAddress.KWH);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        market.login(BATTY, PASSWORD);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertTrue(netty.claimed);
    }

    @Test
    public void acceptExistingOrderDischarge() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(1);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(BATTY, PASSWORD);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertTrue(netty.claimed);
    }

    @Test
    public void almostEmptyBattery() {
        FritzyApiStub market = FritzyApiStub.instance();
        XStorageStub.instance().setStateOfCharge(10);
        Services.get(IMachineDataCacher.class).update();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        BigDecimal eur = new BigDecimal(10);
        market.login(BATTY, PASSWORD);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertFalse(netty.orderRewardRequested);
        assertFalse(netty.claimed);

        // Only buy kWh order made
        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(EContractAddress.KWH.getContractName(),
                market.orders().getOrders().getRecords()[0].getOrder().getTakerAssetData());
    }

    @Test
    public void acceptExistingOrderNoReward() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(1);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(BATTY, PASSWORD);
        netty.rewardToGive = 0;
        bn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertFalse(netty.claimed);
    }

    @Test
    public void acceptExistingOrderChargeBroke() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(1);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.mint(market.getAddress(), kWh, EContractAddress.KWH);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        market.login(BATTY, PASSWORD);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
    }

    @Test
    public void onlyChargeOrderByBatty() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        market.login(BATTY, PASSWORD);
        BigDecimal eur = new BigDecimal(1);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.mint(market.getAddress(), kWh, EContractAddress.KWH);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        netty.rewardToGive = 2;
        bn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);

        // check created orders
        assertEquals(3, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void onlyDischargeOrderByBatty() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        market.login(BATTY, PASSWORD);
        BigDecimal eur = new BigDecimal(1);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        netty.rewardToGive = 2;
        bn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);

        // check created orders
        assertEquals(3, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void chargeOrderTooLargeForGrid() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(2d);
        BigDecimal kWh = new BigDecimal(10d);
        market.mint(market.getAddress(), kWh, EContractAddress.KWH);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        market.login(BATTY, PASSWORD);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
    }

    @Test
    public void dischargeOrderTooLargeForGrid() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(2d);
        BigDecimal kWh = new BigDecimal(10d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(BATTY, PASSWORD);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
    }

    @Test
    public void dischargeOrderTooLargeForBatty() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(2d);
        BigDecimal kWh = new BigDecimal(10d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        // Much capacity but device still cant handle 10kWh in 15 minutes
        NettyApiImpl impl = (NettyApiImpl)Endpoints.get(INettyApi.class);
        impl.gridCapacity = Double.MAX_VALUE;
        impl.groupCapacity = Double.MAX_VALUE;

        market.login(BATTY, PASSWORD);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);

    }

    @Test
    public void cancelExistingOrders() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        // Create orders by batty, market is empty
        market.login(BATTY, PASSWORD);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertEquals(2, market.orders().getOrders().getRecords().length);

        // Create order by sunny for batty to accept
        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(0.04d);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        // Batty accepts the order from sunny
        market.login(BATTY, PASSWORD);
        bn.evaluate();
        assertTrue(netty.claimed);
        assertTrue(netty.orderRewardRequested);
        // Existing orders by batty are gone, only accepted order is left
        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

        // This shouln't create new batty orders, there is an accepted order
        bn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void cancelExistingSunnyAcceptedBatty() {
        FritzyApiStub market = FritzyApiStub.instance();
        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());

        market.login(BATTY, PASSWORD);
        market.mint(market.getAddress(), new BigDecimal(10d), EContractAddress.EUR); // Give batty a bunch of money
        bn.evaluate();
        assertEquals(2, market.orders().getOrders().getRecords().length);
        
        WebOrder order = market.orders().getOrders().getRecords()[0].getOrder();
        market.login(sunny, PASSWORD);
        String sunnyAddr = market.getAddress();
        market.mint(market.getAddress(), new BigDecimal(order.getTakerAssetAmount()),
                EContractAddress.getByContractName(order.getTakerAssetData()));
        market.fillOrder(order.getHash());

        market.login(BATTY, PASSWORD);
        bn.evaluate();

        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getMakerAddress());
        assertEquals(sunnyAddr, market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
        
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);

        bn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);
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
        private double gridCapacity = 16d;
        private double groupCapacity = 32d;
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
