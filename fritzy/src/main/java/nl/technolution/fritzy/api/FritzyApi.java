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

import nl.technolution.Services;
import nl.technolution.dropwizard.webservice.IEndpoint;
import nl.technolution.fritzy.io.IFritzyController;

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
        IFritzyController fritzy = Services.get(IFritzyController.class);
        
        boolean isCooling;
        try {
            isCooling = fritzy.getWebRelay().getState().isRelaystate();
        } catch (IOException e) {
            isCooling = false;
        }

        double temparature = fritzy.getTemperatureSensor().getTemparature();
        return new FritzyState(isCooling, temparature);
    }
}
