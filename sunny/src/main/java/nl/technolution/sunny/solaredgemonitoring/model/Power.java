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
package nl.technolution.sunny.solaredgemonitoring.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * SolarEdge Monitoring portal power object
 */
public class Power {

    @JsonProperty("timeUnit")
    private String timeUnit;
    @JsonProperty("unit")
    private String unit;
    @JsonProperty("measuredBy")
    private String measuredBy;
    @JsonProperty("values")
    private List<Value> values = null;

    @JsonProperty("timeUnit")
    public String getTimeUnit() {
        return timeUnit;
    }

    @JsonProperty("timeUnit")
    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    @JsonProperty("unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("measuredBy")
    public String getMeasuredBy() {
        return measuredBy;
    }

    @JsonProperty("measuredBy")
    public void setMeasuredBy(String measuredBy) {
        this.measuredBy = measuredBy;
    }

    @JsonProperty("values")
    public List<Value> getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(List<Value> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("timeUnit", timeUnit)
                .append("unit", unit)
                .append("measuredBy", measuredBy)
                .append("values", values)
                .toString();
    }
}
