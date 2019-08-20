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
package nl.technolution.netty.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import nl.technolution.DeviceId;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.netty.rewarder.IRewardService;
import nl.technolution.netty.supplylimit.IGridCapacityManager;

/**
 * 
 */
@Path("/netty")
@Produces(MediaType.APPLICATION_JSON)
public class NettyApi implements INettyApi {

    /**
     * Determine grid connection limit of device
     * 
     * @param deviceId to find limit for
     * @return limit in amps
     */
    @Override
    @GET
    @Timed
    @Path("capacity")
    @Produces(MediaType.APPLICATION_JSON)
    public DeviceCapacity getCapacity(@QueryParam("deviceId") String deviceId) {
        DeviceId id = new DeviceId(deviceId);
        IGridCapacityManager gridCapacityManager = Services.get(IGridCapacityManager.class);
        double gridConnectionLimit = gridCapacityManager.getGridConnectionLimit(id);
        double groupConnectionLimit = gridCapacityManager.getGroupConnectionLimit();
        return new DeviceCapacity(gridConnectionLimit, groupConnectionLimit);
    }

    @Override
    public OrderReward getOrderReward(String taker, String orderHash) {
        return Services.get(IRewardService.class).calculateReward(taker, orderHash);
    }

    @Override
    public void claim(String txHash, String rewardId) {
        Services.get(IRewardService.class).claim(txHash, rewardId);
    }
}
