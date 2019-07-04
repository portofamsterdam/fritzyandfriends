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
package nl.technolution.sunny.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.ghgande.j2mod.modbus.ModbusException;

import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.IEndpoint;
import nl.technolution.sunny.solaredge.ISESessionFactory;

/**
 * 
 */
@Path("/sunny")
@Produces(MediaType.APPLICATION_JSON)
public class SunnyApi implements IEndpoint {

    /**
     * Retrieve state of Sunny
     * 
     * @return state of Sunny (power generation)
     */
    @GET
    @Timed
    @Path("state")
    @Produces(MediaType.APPLICATION_JSON)
    public SunnyState getState() {
        try {
            return new SunnyState(Services.get(ISESessionFactory.class).getSESession().getInverterPower());
        } catch (ModbusException e) {
            throw new Error(e.getMessage(), e);
        }
    }
}
