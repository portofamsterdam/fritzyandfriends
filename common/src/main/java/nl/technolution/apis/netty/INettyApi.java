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
package nl.technolution.apis.netty;

import nl.technolution.dropwizard.webservice.IEndpoint;

/**
 * Netty API calls
 */
public interface INettyApi extends IEndpoint {

    /**
     * Determine grid connection limit of device
     * 
     * @param deviceId to find limit for
     * @return limit in amps
     */
    DeviceCapacity getCapacity(String deviceId);


    /**
     * Determine taker reward for a given order.
     * 
     * @param taker of the reward
     * @param orderHash identifying order
     * @return reward
     */
    OrderReward getOrderReward(String taker, String orderHash);

    /**
     * Claim a reward
     * 
     * @param txHash transaction proving acceptance of order
     * @param rewardId reward to claim
     */
    void claim(String txHash, String rewardId);
}