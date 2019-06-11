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
package nl.technolution.exxy.service;

import java.time.Instant;

import nl.technolution.dropwizard.services.IService;
import nl.technolution.exxy.app.ExxyConfig;
import nl.technolution.exxy.service.APXPricesService.NoPricesAvailableException;

/**
 * Defines TransparencyPlatformClient interface
 */
public interface IAPXPricesService extends IService<ExxyConfig> {
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

    /**
     * Get day ahead price in EUR per kWh for the NEXT quarter hour using the cached data.
     * 
     * @return day ahead price per kWh for the next quarter hour
     * @throws NoPricesAvailableException
     */
    double getPricePerkWhNextQuarter() throws NoPricesAvailableException;
}