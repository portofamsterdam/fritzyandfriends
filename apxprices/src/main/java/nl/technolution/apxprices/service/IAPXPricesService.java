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
package nl.technolution.apxprices.service;

import java.time.Instant;

import nl.technolution.apxprices.app.APXPricesConfig;
import nl.technolution.apxprices.service.APXPricesService.NoPricesAvailableException;
import nl.technolution.dropwizard.IService;

/**
 * Defines TransparencyPlatformClient interface
 */
public interface IAPXPricesService extends IService<APXPricesConfig> {
    /**
     * Get day ahead price in EUR per kWh for the requested moment.
     * 
     * NOTE: the cache is bypassed in this case, so this call takes typically some seconds to finish!
     * 
     * @return day ahead price per kWh for the requested moment
     * @throws NoPricesAvailableException
     * 
     */
    double getPricePerkWh(Instant requestedDateTime) throws NoPricesAvailableException;

    /**
     * Get day ahead price in EUR per kWh for the current moment from the cached data.
     * 
     * @return current day ahead price per kWh
     * @throws NoPricesAvailableException
     */
    double getPricePerkWh() throws NoPricesAvailableException;
}