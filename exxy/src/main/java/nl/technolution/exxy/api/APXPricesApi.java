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
package nl.technolution.exxy.api;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import org.hibernate.validator.valuehandling.UnwrapValidatedValue;

import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.IEndpoint;
import nl.technolution.exxy.service.APXPricesService;
import nl.technolution.exxy.service.IAPXPricesService;

import io.dropwizard.jersey.params.InstantParam;

/**
 * 
 */
@Path("exxy/")
@Produces(MediaType.APPLICATION_JSON)
public class APXPricesApi implements IEndpoint {

    /**
     * Get day ahead price in EUR per kWh for the current moment using the cache.
     * 
     * @return day ahead price for the current moment
     */
    @GET
    @Timed
    @Path("currentPrice")
    @Produces(MediaType.APPLICATION_JSON)
    public ApxPrice getCurrentPrice() {
        IAPXPricesService priceService = Services.get(IAPXPricesService.class);
        try {
            return new ApxPrice(priceService.getPricePerkWh());
        } catch (APXPricesService.NoPricesAvailableException e) {
            throw new WebApplicationException(e.getMessage(), e);
        }
    }

    /**
     * Get day ahead price in EUR per kWh for the NEXT quarter hour using the cache.
     * 
     * @return day ahead price for the next quarter hour (request at 12:01 gives price for 12:15).
     */
    @GET
    @Timed
    @Path("nextQuarterPrice")
    @Produces(MediaType.APPLICATION_JSON)
    public ApxPrice getNextQuarterHourPrice() {
        IAPXPricesService priceService = Services.get(IAPXPricesService.class);
        try {
            return new ApxPrice(priceService.getPricePerkWh());
        } catch (APXPricesService.NoPricesAvailableException e) {
            throw new WebApplicationException(e.getMessage(), e);
        }
    }

    /**
     * Get day ahead price in EUR per kWh for the requested moment.
     * 
     * NOTE: the cache is bypassed in this case, so this call takes typically some seconds to finish!
     * 
     * @return day ahead price for the requested moment
     */
    @GET
    @Timed
    @Path("price")
    @Produces(MediaType.APPLICATION_JSON)
    public ApxPrice getPrice(
            @QueryParam(value = "dateTime") @NotNull @UnwrapValidatedValue InstantParam requestedDateTime) {
        IAPXPricesService priceService = Services.get(IAPXPricesService.class);
        try {
            return new ApxPrice(priceService.getPricePerkWh(requestedDateTime.get()));
        } catch (APXPricesService.NoPricesAvailableException e) {
            throw new WebApplicationException(e.getMessage(), e);
        }
    }
}
