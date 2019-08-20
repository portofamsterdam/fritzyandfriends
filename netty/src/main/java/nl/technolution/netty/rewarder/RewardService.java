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

import java.math.BigDecimal;
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

import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.netty.app.NettyConfig;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public final class RewardService implements IRewardService {

    private NettyConfig config;
    private Map<String, OrderReward> rewards = Maps.newHashMap();

    @Override
    public void init(NettyConfig config) {
        this.config = config;
    }

    @Override
    public OrderReward calculateReward(String taker, String orderHash) {
        IFritzyApi market = Services.get(IFritzyApiFactory.class).build();
        cleanOldReward();
        OrderReward existingReward = getExistingReward(taker, orderHash);
        if (existingReward != null) {
            return existingReward;
        }
        WebOrder order = market.order(orderHash);
        if (order == null) {
            // Do not store empty rewards, the order may exist later
            return OrderReward.none(taker, orderHash);
        }
        if (isLocal(taker, order, market)) {
            return calculateReward(taker, order);
        }
        return OrderReward.none(taker, orderHash);
    }

    private OrderReward getExistingReward(String taker, String orderHash) {
        OrderReward existingReward = rewards.values().stream()
                .filter(o -> o.getClaimTaker().equals(taker))
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
        order.setClaimTaker(taker);
        order.setOrderHash(weborder.getHash());
        order.setReward(config.getLocalReward());
        order.setRewardId(UUID.randomUUID().toString());
        rewards.put(order.getRewardId(), order);
        return order;
    }

    private boolean isLocal(String taker, WebOrder orderHash, IFritzyApi market) {
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
        return isLocal(uMaker.getName(), uTaker.getName());
    }

    @Override
    public boolean isLocal(String makerUsername, String takerUsername) {
        Set<String> localusers = config.getLocalusers();
        return makerUsername != null && takerUsername != null && localusers.contains(takerUsername) &&
                localusers.contains(makerUsername);
    }

    @Override
    public void claim(String orderHash, String rewardId) {
        OrderReward reward = rewards.get(orderHash);
        if (reward == null) {
            return;
        }

        IFritzyApi market = Services.get(IFritzyApiFactory.class).build();
        WebOrder order = market.order(orderHash);

        if (order.getTakerAddress() != null && order.getTakerAddress().equals(reward.getClaimTaker())) {
            payReward(market, reward);
        }
    }

    private void payReward(IFritzyApi market, OrderReward reward) {
        market.transfer(BigDecimal.valueOf(reward.getReward()), EContractAddress.EUR, reward.getClaimTaker());
        rewards.remove(reward.getOrderHash());
    }
}
