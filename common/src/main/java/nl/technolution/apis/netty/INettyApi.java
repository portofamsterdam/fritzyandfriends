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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import nl.technolution.dropwizard.webservice.IEndpoint;

/**
 * Netty API calls
 */
@Path("/netty")
@Produces(MediaType.APPLICATION_JSON)
public interface INettyApi extends IEndpoint {

    /**
     * Determine grid connection limit of device
     * 
     * @param deviceId to find limit for
     * @return limit in amps
     */
    @GET
    @Timed
    @Path("capacity")
    @Produces(MediaType.APPLICATION_JSON)
    DeviceCapacity getCapacity(@QueryParam("deviceId") String deviceId);

    /**
     * Determine taker reward for a given order.
     * 
     * @param taker of the reward
     * @param orderHash identifying order
     * @return reward
     */
    @GET
    @Timed
    @Path("orderReward")
    @Produces(MediaType.APPLICATION_JSON)
    OrderReward getOrderReward(@QueryParam("taker") String taker, @QueryParam("orderHash") String orderHash);

    /**
     * Claim a reward
     * 
     * @param txHash transaction proving acceptance of order
     * @param rewardId reward to claim
     */
    @GET
    @Timed
    @Path("claim")
    void claim(@QueryParam("txHash") String txHash, @QueryParam("rewardId") String rewardId);
}