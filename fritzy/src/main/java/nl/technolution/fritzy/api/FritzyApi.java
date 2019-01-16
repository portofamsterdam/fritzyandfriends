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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import nl.technolution.IEndpoint;
import nl.technolution.Services;
import nl.technolution.fritzy.IFritzy;
import nl.technolution.fritzy.tempsensor.TemperatureSensor;
import nl.technolution.fritzy.webrelay.WebRelayState;

/**
 * 
 */
@Path("/fritzy")
@Produces(MediaType.APPLICATION_JSON)
public class FritzyApi implements IEndpoint {

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
        IFritzy fritzy = Services.get(IFritzy.class);
        WebRelayState state;
        try {
            state = fritzy.getWebRelay().getState();
        } catch (IOException e) {
            // TODO MKE create clear errors
            throw new WebApplicationException(e);
        }

        boolean isCooling = state.isRelaystate();

        TemperatureSensor temperatureSensor = fritzy.getTemperatureSensor();
        // TODO MKE temparature sensor
        double temparature = temperatureSensor.getTemparature();
        return new FritzyState(isCooling, temparature);
    }
}
