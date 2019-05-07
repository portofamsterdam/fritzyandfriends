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
import nl.technolution.fritzy.io.tempsensor.TemperatureSensor;
import nl.technolution.fritzy.io.webrelay.WebRelay;

/**
 * Defines Fritzy object
 */
public interface IFritzyController extends IService<FritzyConfig> {

    /**
     * Get the webrelay of the fridge to start or end cooling
     * 
     * @return webrelay
     */
    WebRelay getWebRelay();

    /**
     * Get the temparature sensor of the fridge
     * 
     * @return temparature sensor
     */
    TemperatureSensor getTemperatureSensor();
}