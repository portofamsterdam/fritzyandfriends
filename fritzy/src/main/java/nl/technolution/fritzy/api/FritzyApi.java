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
package nl.technolution.fritzy.api;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.IEndpoint;
import nl.technolution.fritzy.io.IIoFactory;

/**
 * 
 */
@Path("/fritzy")
@Produces(MediaType.APPLICATION_JSON)
public class FritzyApi implements IEndpoint {

    private final Logger log = Log.getLogger();

    /**
     * Retrieve state of Fritzy
     * 
     * @return state of cooler and temparature
     */
    @GET
    @Timed
    @Path("state")
    @Produces(MediaType.APPLICATION_JSON)
    public FritzyState getState() {
        IIoFactory fritzy = Services.get(IIoFactory.class);
        boolean isCooling;
        try {
            isCooling = fritzy.getWebRelay().getState().isRelaystate();
        } catch (IOException e) {
            log.warn("Unable to read relay state", e);
            isCooling = false;
        }

        double temparature = fritzy.getTemparatureSensor().getTemparature();
        return new FritzyState(isCooling, temparature);
    }
}
