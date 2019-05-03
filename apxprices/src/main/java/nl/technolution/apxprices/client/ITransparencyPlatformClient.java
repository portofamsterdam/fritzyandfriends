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

/**
 * Defines TransparencyPlatformClient interface
 */
public interface ITransparencyPlatformClient {

	/**
     * Get day ahead prices for next 24 hours
     * 
     * 
     * https://transparency.entsoe.eu//api?securityToken=0b1d9ae3-d9a6-4c6b-8dc1-c62a18387ac5&documentType=A44&in_Domain=10YNL----------L&out_Domain=10YNL----------L&TimeInterval=2019-04-10T10%3A00Z%2F2019-04-11T10%3A00Z
     * 
     * @return
     */
	PublicationMarketDocument getDayAheadPrices(Instant requestedDateTime);
}