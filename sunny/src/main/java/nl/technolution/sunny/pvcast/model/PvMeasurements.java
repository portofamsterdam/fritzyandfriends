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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * PvMeasurements object
 */
public class PvMeasurements {

    @JsonProperty("measurements")
    private Measurements measurements;

    @JsonProperty("measurements")
    public Measurements getMeasurements() {
        return measurements;
    }

    @JsonProperty("measurements")
    public void setMeasurements(Measurements measurements) {
        this.measurements = measurements;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("measurements", measurements).toString();
    }

}
