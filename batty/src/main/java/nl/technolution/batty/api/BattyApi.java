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
package nl.technolution.batty.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.IEndpoint;

/**
 * 
 */
@Path("/batty")
@Produces(MediaType.APPLICATION_JSON)
public class BattyApi implements IEndpoint {

    /**
     * Retrieve state of Fritzy
     * 
     * @return state of cooler and temparature
     */
    @GET
    @Timed
    @Path("state")
    @Produces(MediaType.APPLICATION_JSON)
    public BattyState getState() {
        try {
            MachineData machineData = Services.get(IMachineDataCacher.class).getMachineData();
            int soc = machineData.getSoc();
            return new BattyState("on", soc);
        } catch (RuntimeException ex) {
            return new BattyState("unreachable", -1);
        }
    }
}
