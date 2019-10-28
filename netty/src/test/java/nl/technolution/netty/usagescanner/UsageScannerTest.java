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
package nl.technolution.netty.usagescanner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.JacksonFactory;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.LimitData;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.netty.app.NettyConfig;
import nl.technolution.netty.rewarder.IRewardService;
import nl.technolution.netty.rewarder.RewardService;
import nl.technolution.netty.supplylimit.GridConnectionManager;
import nl.technolution.netty.supplylimit.IGridCapacityManager;

/**
 * 
 */
public class UsageScannerTest {

    private static final double MAX = 12d;

    @Before
    public void setup() {
        FritzyApiFactory localFactory = new FritzyApiFactory();
        String username = "test";
        MarketConfig marketConfig = new MarketConfig(true, "", username, "password");
        FritzyAppConfig fritzyAppConfig = new FritzyAppConfig();
        fritzyAppConfig.setMarket(marketConfig);
        localFactory.init(fritzyAppConfig);
        Services.put(IFritzyApiFactory.class, localFactory);

        FritzyApiStub.reset();
        FritzyApiStub.instance().register(username, username, "password");

        GridConnectionManager gridManager = new GridConnectionManager();
        NettyConfig nettyconfig = new NettyConfig();
        nettyconfig.setDefaultGridConnectionLimit(8.0d);
        nettyconfig.setGroupConnectionLimit(MAX);
        nettyconfig.setDeviceLimits(Maps.newHashMap());
        gridManager.init(nettyconfig);
        Services.put(IGridCapacityManager.class, gridManager);

        RewardService rewardService = new RewardService();
        nettyconfig.setLocalusers(Sets.newHashSet());
        rewardService.init(nettyconfig);
        Services.put(IRewardService.class, rewardService);
    }

    @Test
    public void testUsageScanner() throws JsonParseException, JsonMappingException, IOException {
        UsageScanner s = new UsageScanner();
        FritzyApiStub instance = FritzyApiStub.instance();
        s.execute();
        String eventData = instance.getAllEvents()
                .stream()
                .filter(e -> e.getTag().equals(EEventType.LIMIT_TOTAL.getTag()))
                .map(e -> e.getData())
                .findFirst().get();
        double actMax = JacksonFactory.defaultMapper().readValue(eventData, LimitData.class).getLimit().doubleValue();
        assertEquals(actMax, MAX, 0.000d);
        String maker = instance.register("maker@test.nl", "maker", "").getAddress();
        String taker = instance.register("taker@test.nl", "raker", "").getAddress();
        String order = instance.mockCompleteOrder(maker, taker, BigDecimal.valueOf(13d), EContractAddress.KWH,
                BigDecimal.valueOf(1), EContractAddress.EUR);
        instance.log(EEventType.ORDER_ACCEPT, order, null);
        s.execute();
        instance.getAllEvents()
                .stream()
                .peek(e -> System.out.println("" + e))
                .filter(e -> e.getTag().equals(EEventType.LIMIT_EXCEEDED.getTag()))
                .findAny().orElseThrow(AssertionError::new);
    }
}
