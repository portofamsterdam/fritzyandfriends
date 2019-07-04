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

import nl.technolution.apis.netty.OrderReward;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApi;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public class RewardService implements IRewardService {

    private FritzyApi market;

    @Override
    public void init(NettyConfig config) {
        market = new FritzyApi(config.getMarket().getMarketUrl(), config.getEnvironment());
        market.login(config.getMarket().getEmail(), config.getMarket().getPassword());
    }

    @Override
    public OrderReward calculateReward(String taker, String orderHash) {
        WebOrder order = market.order(orderHash);
        if (order == null) {
            return OrderReward.NONE;
        }
        return null;
    }

    @Override
    public void claim(String txHash, String rewardId) {
        //

    }
}
