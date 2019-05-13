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
package nl.technolution.apxprices;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import nl.technolution.dropwizard.services.ServiceFinder;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.exxy.app.ExxyConfig;
import nl.technolution.exxy.service.APXPricesService;
import nl.technolution.exxy.service.IAPXPricesService;
import nl.technolution.exxy.service.APXPricesService.NoPricesAvailableException;

/**
 * Tests for APXPriceService
 * 
 */
public class APXPriceServiceTool {

    public static void main(String[] args) throws NoPricesAvailableException {

        ExxyConfig config = new ExxyConfig("https://transparency.entsoe.eu/api",
                "0b1d9ae3-d9a6-4c6b-8dc1-c62a18387ac5", null, false);
        ServiceFinder.setupServices(config);
        IAPXPricesService priceService = Services.get(IAPXPricesService.class);
        getPrice(priceService);
    }


    /**
     * Test getPrice method using the real entsoe server. The data seems to be available for the past 5 years so this
     * testcase will probably fail in 2024...
     * 
     * The expected values are retrived looking at the graph on the website:
     * https://transparency.entsoe.eu/transmission-domain/r2/dayAheadPrices/show?name=&defaultValue=false
     * &viewType=GRAPH&areaType=BZN&atch=false&dateTime.dateTime=24.04.2019+00:00|UTC|DAY&
     * biddingZone.values=CTY|10YNL----------L!BZN|10YNL----------L&dateTime.timezone=UTC&dateTime.timezone_input=UTC
     * 
     * @param priceService
     * 
     * @throws NoPricesAvailableException
     */
    private static void getPrice(IAPXPricesService priceService) throws NoPricesAvailableException {
        // next should give the price at midnight(UTC) at 1-1-2019 (which is 64,98 EUR per MWH)
        Instant instant = Instant.parse("2019-01-01T00:00:00.00Z");
        double price = priceService.getPricePerkWh(instant);
        System.out.println("price per kWh for " + instant + ": " + price);
        // assertEquals(64.98d / 1000, price, 0.001);
        // idem
        instant = Instant.parse("2019-01-01T00:59:59.99Z");
        price = priceService.getPricePerkWh(instant);
        System.out.println("price per kWh for " + instant + ": " + price);
        // assertEquals(64.98d / 1000, price, 0.001);
        // next should give the price at 12:00(UTC) at 24-4-2019 (which is 31,6 EUR per MWH)
        instant = Instant.parse("2019-04-24T12:00:00.00Z");
        price = priceService.getPricePerkWh(instant);
        System.out.println("price per kWh for " + instant + ": " + price);
        // assertEquals(31.6d / 1000, price, 0.001);
    }

    private static void noPricesAvailableTest(IAPXPricesService priceService) throws NoPricesAvailableException {
        // next should fail with an exception
        Instant instant = Instant.parse("1919-01-01T00:00:00.00Z");
        priceService.getPricePerkWh(instant);
    }

    private static void fixedPricesTest(IAPXPricesService priceService) throws NoPricesAvailableException {
        // setup config with fixed prices
        Map<Integer, Double> fixedPrices = new HashMap<Integer, Double>();
        for (int i = 0; i < 24; i++) {
            fixedPrices.put(i, (double)i / 100);
        }
        ExxyConfig config = new ExxyConfig("", "", null, true);
        priceService = new APXPricesService();
        // manually init the service
        priceService.init(config);

        Instant instant = OffsetDateTime.now().withHour(8).toInstant();
        double price = priceService.getPricePerkWh(instant);
        // assertEquals(0.08d, price, 0.001);

        price = priceService.getPricePerkWh();
        // assertEquals((double)LocalTime.now().get(ChronoField.HOUR_OF_DAY) / 100, price, 0.001);
    }
}
