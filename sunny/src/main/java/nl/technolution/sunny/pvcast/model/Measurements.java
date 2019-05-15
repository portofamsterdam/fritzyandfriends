/*
 (C) COPYRIGHT 2019 TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.pvcast.model;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Measurements object. The keys of the Power object are unix timestamps.
 */
public class Measurements {
    // NOTE: a TreeMap is used so entries are sorted, this to ease debugging
    @JsonValue
    private Map<Long, Power> measurements = new TreeMap<Long, Power>();

    /**
     * Put power object into measurements map.
     * 
     * @param timestamp
     * @param power
     */
    public void setMeasurement(Long timestamp, Power power) {
        measurements.put(timestamp, power);
    }
}
