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
import nl.technolution.dropwizard.services.IService;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public interface IRewardService extends IService<NettyConfig> {

    /**
     * @param taker of the reward
     * @param orderHash on public market to find
     * @return offered reward
     */
    OrderReward calculateReward(String taker, String orderHash);

    /**
     * @param txHash taker reward
     * @param rewardId identifier of reward promis created earlier
     */
    void claim(String txHash, String rewardId);

    /**
     * Check if a transaction is local by check the users
     * 
     * @param makerUsername to check
     * @param takerUsername to check
     * @return true if local
     */
    boolean isLocal(String makerUsername, String takerUsername);

}
