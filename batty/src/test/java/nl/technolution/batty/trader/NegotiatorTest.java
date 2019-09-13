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
    public void acceptExistingOrder() {
        FritzyApiStub market = FritzyApiStub.instance();
        BattyResourceHelper resourceHelper = new BattyResourceHelper(DEVICE_ID);
        bn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate());
        bn.flexibilityUpdate(resourceHelper.getStorageSystemDescription());
        
        
        String sunny = "sunny";
        market.register(sunny , sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = new BigDecimal(1);
        BigDecimal kWh = new BigDecimal(0.125d);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        market.login(BATTY, PASSWORD);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        netty.rewardToGive = 2;
        bn.evaluate();
        assertTrue(netty.claimed);
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
