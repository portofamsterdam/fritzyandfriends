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
import nl.technolution.Services;
import nl.technolution.dropwizard.webservice.IEndpoint;
import nl.technolution.netty.supplylimit.IGridCapacityManager;

/**
 * 
 */
@Path("/netty")
@Produces(MediaType.APPLICATION_JSON)
public class NettyApi implements IEndpoint {

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
    public double getCapacity(@QueryParam("deviceId") String deviceId) {
        return Services.get(IGridCapacityManager.class).getGridConnectionLimit(new DeviceId(deviceId));
    }
}
