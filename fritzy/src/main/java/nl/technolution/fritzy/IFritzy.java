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
package nl.technolution.fritzy;

import nl.technolution.fritzy.tempsensor.TemperatureSensor;
import nl.technolution.fritzy.webrelay.WebRelay;

/**
 * Defines Fritzy object
 */
public interface IFritzy {

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
