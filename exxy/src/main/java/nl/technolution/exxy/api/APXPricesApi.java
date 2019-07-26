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

import io.dropwizard.jersey.params.InstantParam;
import nl.technolution.apis.exxy.ApxPrice;
import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.exxy.service.APXPricesService;
import nl.technolution.exxy.service.IAPXPricesService;

/**
 * 
 */
@Path("exxy/")
@Produces(MediaType.APPLICATION_JSON)
public class APXPricesApi implements IAPXPricesApi {

    @GET
    @Timed
    @Path("currentPrice")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public ApxPrice getCurrentPrice() {
        IAPXPricesService priceService = Services.get(IAPXPricesService.class);
        try {
            return new ApxPrice(priceService.getPricePerkWh());
        } catch (APXPricesService.NoPricesAvailableException e) {
            throw new WebApplicationException(e.getMessage(), e);
        }
    }


    @GET
    @Timed
    @Path("nextQuarterPrice")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public ApxPrice getNextQuarterHourPrice() {
        IAPXPricesService priceService = Services.get(IAPXPricesService.class);
        try {
            return new ApxPrice(priceService.getPricePerkWh());
        } catch (APXPricesService.NoPricesAvailableException e) {
            throw new WebApplicationException(e.getMessage(), e);
        }
    }


    @GET
    @Timed
    @Path("price")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
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
