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
package nl.technolution.apis.exxy;

import io.dropwizard.jersey.params.InstantParam;
import nl.technolution.dropwizard.webservice.IEndpoint;

/**
 * 
 */
public interface IAPXPricesApi extends IEndpoint {

    /**
     * Get day ahead price in EUR per kWh for the current moment using the cache.
     * 
     * @return day ahead price for the current moment
     */
    ApxPrice getCurrentPrice();

    /**
     * Get day ahead price in EUR per kWh for the NEXT quarter hour using the cache.
     * 
     * @return day ahead price for the next quarter hour (request at 12:01 gives price for 12:15).
     */
    ApxPrice getNextQuarterHourPrice();

    /**
     * Get day ahead price in EUR per kWh for the requested moment.
     * 
     * NOTE: the cache is bypassed in this case, so this call takes typically some seconds to finish!
     * 
     * @return day ahead price for the requested moment
     */
    ApxPrice getPrice(InstantParam requestedDateTime);

}
