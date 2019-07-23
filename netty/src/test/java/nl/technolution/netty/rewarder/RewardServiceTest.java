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
package nl.technolution.netty.rewarder;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.fritzy.wallet.order.Order;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public class RewardServiceTest {

    private static final String NETTY = "netty@test.be";
    private static final String USERA = "A@test.be";
    private static final String USERB = "B@test.be";

    @Before
    public void setup() {
        FritzyApiFactory service = new FritzyApiFactory();
        MarketConfig marketConfig = new MarketConfig(true, "", NETTY, "");
        service.init(new FritzyAppConfig(RewardServiceTest.class.getSimpleName(), null, marketConfig));
        Services.put(IFritzyApiFactory.class, service);

        // Create user netty with some funds
        IFritzyApi api = service.build();
        api.register(NETTY, NETTY, "");
        String nettyAddr = Arrays.asList(api.getUsers()).stream().findFirst().get().getAddress();
        api.login(NETTY, "");
        api.mint(nettyAddr, BigDecimal.valueOf(1000), EContractAddress.EUR);
    }

    /**
     * 
     */
    @Test
    public void testRewards() {
        IFritzyApi api = Services.get(IFritzyApiFactory.class).build();
        // Create local users
        WebUser userA = api.register(USERA, USERA, "");
        WebUser userB = api.register(USERB, USERB, "");

        // Give them stuff
        api.login(NETTY, "");
        api.mint(userA.getAddress(), BigDecimal.valueOf(100), EContractAddress.EUR);
        api.mint(userB.getAddress(), BigDecimal.valueOf(100), EContractAddress.KWH);
        
        api.login(USERA, "");
        assertEquals(0L, api.balance().getKwh().longValue());
        assertEquals(100L, api.balance().getEur().longValue());

        RewardService s = new RewardService();
        NettyConfig config = new NettyConfig();
        MarketConfig marketConfig = new MarketConfig(true, "", NETTY, "");
        config.setMarket(marketConfig);
        double localReward = 1d;
        config.setLocalReward(localReward);
        config.setLocalusers(Sets.newHashSet(USERA, USERB));
        s.init(config);

        String fakeOrderHash = "0x3456faef7b89f789beaf676baf806bf89bae7f8";
        OrderReward reward = s.calculateReward(userA.getAddress(), fakeOrderHash);
        assertEquals(0d, reward.getReward(), 0.0001d);
        assertEquals(fakeOrderHash, reward.getOrderHash());

        // Create an order as user b
        api.login(USERB, "");
        Order order = new Order();
        order.setMakerAmount("5");
        order.setMakerToken(EContractAddress.KWH.name());
        order.setTakerAmount("1");
        order.setTakerToken(EContractAddress.EUR.name());
        String orderHash = api.createOrder(order);

        api.login(NETTY, "");
        reward = s.calculateReward(userA.getAddress(), orderHash);
        assertEquals(1d, reward.getReward(), 0.0001d);
        assertEquals(orderHash, reward.getOrderHash());

        api.login(USERA, "");
        String txId = api.fillOrder(orderHash);

        api.login(NETTY, "");
        s.claim(orderHash, txId);

        api.login(USERA, "");
        assertEquals(new BigDecimal(5), api.balance().getKwh());
        assertEquals(new BigDecimal(99), api.balance().getEur());
    }
}
