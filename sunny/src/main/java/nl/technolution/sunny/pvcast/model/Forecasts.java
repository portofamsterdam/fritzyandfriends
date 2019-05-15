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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Forecasts object. The keys of the forecasts object are unix timestamps.
 */
public class Forecasts {
    // NOTE: a Treemap is used to entries are sorted, this to ease debugging
    private Map<Long, Forecast> forecasts = new TreeMap<Long, Forecast>();

    public Map<Long, Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(Map<Long, Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("forecasts", forecasts).toString();
    }
}
