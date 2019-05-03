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
package nl.technolution.apxprices.client;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.junit.Test;

import nl.technolution.apxprices.app.APXPricesConfig;
import nl.technolution.apxprices.service.APXPricesService;
import nl.technolution.apxprices.service.IAPXPricesService;

/**
 * Test TransparencyPlatformClient 
 * 
 */
public class APXPriceServiceTest {

    /**
     * Test getPrice method using the real entsoe server. The data seems to be available for the past 5 years so this 
     * testcase will probably fail in 2024...
     * 
     * The expected values are retrived looking at the graph on the website: 
     * https://transparency.entsoe.eu/transmission-domain/r2/dayAheadPrices/show?name=&defaultValue=false&viewType=GRAPH&areaType=BZN&atch=false&dateTime.dateTime=24.04.2019+00:00|UTC|DAY&biddingZone.values=CTY|10YNL----------L!BZN|10YNL----------L&dateTime.timezone=UTC&dateTime.timezone_input=UTC
     */
    @Test
    public void getPriceTest() {
        APXPricesConfig config = new APXPricesConfig("https://transparency.entsoe.eu/api", "0b1d9ae3-d9a6-4c6b-8dc1-c62a18387ac5");
        IAPXPricesService priceService = new APXPricesService();
        // manually init the service
        priceService.init(config);
    	// next should give the price at midnight(UTC) at 1-1-2019 (which is 64,98 EUR per MWH)  
    	Instant instant = Instant.parse("2019-01-01T00:00:00.00Z");
    	double price = priceService.getPricePerkWh(instant);
    	System.out.println("price per kWh for " + instant + ": " + price);
        assertEquals(64.98d/1000, price, 0.001);
        // idem
    	instant = Instant.parse("2019-01-01T00:59:59.99Z");
    	price = priceService.getPricePerkWh(instant);
    	System.out.println("price per kWh for " + instant + ": " + price);
        assertEquals(64.98d/1000, price, 0.001);
    	// next should give the price at 12:00(UTC) at 24-4-2019 (which is 31,6 EUR per MWH)  
    	instant = Instant.parse("2019-04-24T12:00:00.00Z");
    	price = priceService.getPricePerkWh(instant);
    	System.out.println("price per kWh for " + instant + ": " + price);
        assertEquals(31.6d/1000, price, 0.001);
    }
}
