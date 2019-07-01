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
    private final String rewardId;

    @JsonProperty("reward")
    private final double reward;

    @JsonProperty("expireTs")
    private final LocalDateTime expireTs;

    /**
     * Constructor for {@link OrderReward} objects
     * 
     * @param reward value
     */
    public OrderReward(String rewardId, double reward, LocalDateTime expireTs) {
        this.rewardId = rewardId;
        this.reward = reward;
        this.expireTs = expireTs;
    }

    public String getRewardId() {
        return rewardId;
    }

    public double getReward() {
        return reward;
    }

    public LocalDateTime getExpireTs() {
        return expireTs;
    }

}
