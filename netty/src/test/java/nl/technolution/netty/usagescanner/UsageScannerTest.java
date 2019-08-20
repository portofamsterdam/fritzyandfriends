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

import java.math.BigDecimal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
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
        MarketConfig marketConfig = new MarketConfig(true, "", "test", "password");
        FritzyAppConfig fritzyAppConfig = new FritzyAppConfig();
        fritzyAppConfig.setMarket(marketConfig);
        localFactory.init(fritzyAppConfig);
        Services.put(IFritzyApiFactory.class, localFactory);

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
    public void testUsageScanner() {
        UsageScanner s = new UsageScanner();
        FritzyApiStub instance = FritzyApiStub.instance();

        s.execute();
        double reportedMax = instance.getAllEvents()
                .stream()
                .filter(e -> e.getTag().equals(EEventType.LIMIT_TOTAL.getTag()))
                .map(e -> e.getMsg())
                .map(Double::valueOf)
                .findFirst().get();
        assertEquals(reportedMax, MAX, 0.000d);
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
