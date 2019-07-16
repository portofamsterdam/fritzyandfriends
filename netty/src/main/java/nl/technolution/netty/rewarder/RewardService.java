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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;

import org.glassfish.jersey.internal.guava.Sets;

import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.netty.app.NettyConfig;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public final class RewardService implements IRewardService {

    private IFritzyApi market;

    private Set<String> localusers = Sets.newHashSet();
    private Map<String, OrderReward> rewards = Maps.newHashMap();
    private double localReward;

    @Override
    public void init(NettyConfig config) {
        market = Services.get(IFritzyApiFactory.class).build();
        market.login(config.getMarket().getEmail(), config.getMarket().getPassword());
        localusers = config.getLocalusers();
        localReward = config.getLocalReward();
    }

    @Override
    public OrderReward calculateReward(String taker, String orderHash) {
        cleanOldReward();
        OrderReward existingReward = getExistingReward(taker, orderHash);
        if (existingReward != null) {
            return existingReward;
        }
        WebOrder order = market.order(orderHash);
        if (order == null) {
            // Do not store empty orders, the order may exist later
            return OrderReward.none(taker, orderHash);
        }
        if (isLocal(taker, order)) {
            return calculateReward(taker, order);
        }
        return OrderReward.none(taker, orderHash);
    }

    private OrderReward getExistingReward(String taker, String orderHash) {
        OrderReward existingReward = rewards.values().stream()
                .filter(o -> o.getTaker().equals(taker))
                .filter(o -> o.getOrderHash().equals(orderHash))
                .findFirst().orElse(null);
        if (existingReward != null) {
            return existingReward;
        }
        return null;
    }

    private void cleanOldReward() {
        Iterator<Entry<String, OrderReward>> itr = rewards.entrySet().iterator();
        while (itr.hasNext()) {
            if (itr.next().getValue().getExpireTs().isAfter(LocalDateTime.now())) {
                itr.remove();
            }
        }
    }

    private OrderReward calculateReward(String taker, WebOrder weborder) {
        Instant nextQuarter = Efi.getNextQuarter();
        LocalDateTime localNextQuarter = LocalDateTime.ofInstant(nextQuarter, ZoneId.systemDefault());
        OrderReward order = new OrderReward();
        order.setExpireTs(localNextQuarter);
        order.setTaker(taker);
        order.setOrderHash(weborder.getHash());
        order.setReward(localReward);
        order.setRewardId(UUID.randomUUID().toString());
        rewards.put(order.getRewardId(), order);
        return order;
    }

    private boolean isLocal(String taker, WebOrder orderHash) {
        WebUser uTaker = null;
        WebUser uMaker = null;
        for (WebUser user : Arrays.asList(market.getUsers())) {
            if (user.getAddress().equals(taker)) {
                uTaker = user;
                continue;
            }
            if (user.getAddress().equals(orderHash.getMakerAddress())) {
                uMaker = user;
                continue;
            }
        }
        return uMaker != null && uTaker != null 
                && localusers.contains(uTaker.getName()) && localusers.contains(uMaker.getName());
    }

    @Override
    public void claim(String txHash, String rewardId) {

        // TODO MKE transfer reward to claimer

    }
}
