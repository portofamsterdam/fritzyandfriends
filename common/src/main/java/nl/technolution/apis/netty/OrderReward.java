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

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.IJsonnable;

/**
 * 
 */
public class OrderReward implements IJsonnable {

    @JsonProperty("rewardId")
    private String rewardId;

    @JsonProperty("reward")
    private double reward;

    @JsonProperty("taker")
    private String taker;

    @JsonProperty("orderHash")
    private String orderHash;

    @JsonProperty("expireTs")
    private LocalDateTime expireTs;

    /**
     * Create a reward without any value
     * 
     * @param taker of the order
     * @param txId of order where the reward is for
     * @return
     */
    public static OrderReward none(String taker, String orderHash) {
        OrderReward r = new OrderReward();
        r.setExpireTs(LocalDateTime.now());
        r.setReward(0.0d);
        r.setRewardId("");
        r.setTaker(taker);
        r.setOrderHash(orderHash);
        return r;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public String getTaker() {
        return taker;
    }

    public void setTaker(String taker) {
        this.taker = taker;
    }

    public String getOrderHash() {
        return orderHash;
    }

    public void setOrderHash(String orderHash) {
        this.orderHash = orderHash;
    }

    public LocalDateTime getExpireTs() {
        return expireTs;
    }

    public void setExpireTs(LocalDateTime expireTs) {
        this.expireTs = expireTs;
    }
}
