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

import java.time.Instant;

import nl.technolution.apxprices.app.APXPricesConfig;
import nl.technolution.dropwizard.services.IService;

/**
 * Defines TransparencyPlatformClient interface
 */
public interface ITransparencyPlatformClient extends IService<APXPricesConfig> {

    /**
     * Get day ahead prices for next 24 hours
     * 
     * @return
     */
    PublicationMarketDocument getDayAheadPrices(Instant requestedDateTime);
}