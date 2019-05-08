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
package nl.technolution.fritzy.io;

import nl.technolution.dropwizard.services.IService;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.io.tempsensor.ITemperatureSensor;
import nl.technolution.fritzy.io.webrelay.IWebRelay;

/**
 * 
 */
public interface IIoFactory extends IService<FritzyConfig> {

    /**
     * Get temparature sensor
     * 
     * @return instance
     */
    ITemperatureSensor getTemparatureSensor();

    /**
     * Get webrelay to switch fridge
     * 
     * @return relay
     */
    IWebRelay getWebRelay();
}
