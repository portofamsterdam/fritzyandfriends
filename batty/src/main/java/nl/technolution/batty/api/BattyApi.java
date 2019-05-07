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

import nl.technolution.Services;
import nl.technolution.batty.xstorage.IXStorageConnection;
import nl.technolution.batty.xstorage.IXStorageFactory;
import nl.technolution.batty.xstorage.XStorageException;
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
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        try {
            int soc = connection.getMachineData().getSoc();
            return new BattyState("on", soc);
        } catch (XStorageException ex) {
            return new BattyState("unreachable", -1);
        }
    }
}
