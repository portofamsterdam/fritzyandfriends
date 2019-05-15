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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Forecasts object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "power", "globalradiation", "temperature", "precipitation", "globalradiation_clearsky" })
public class Forecast {
    // power
    // Integer: power in watts. If there is no forecast available yet, the power key and its value are missing.
    @JsonProperty("power")
    private Integer power;
    // globalradiation
    // Integer: globalradiation in watts
    @JsonProperty("globalradiation")
    private Integer globalradiation;
    // temperature
    // Real Number: temperature in °C
    @JsonProperty("temperature")
    private Double temperature;
    // precipitation
    // Real Number: precipitation in mm
    @JsonProperty("precipitation")
    private Integer precipitation;
    // globalradiation_clearsky
    // Integer: globalradiation under clear sky in watts
    @JsonProperty("globalradiation_clearsky")
    private Integer globalradiationClearsky;

    @JsonProperty("globalradiation")
    public Integer getGlobalradiation() {
        return globalradiation;
    }

    @JsonProperty("globalradiation")
    public void setGlobalradiation(Integer globalradiation) {
        this.globalradiation = globalradiation;
    }

    @JsonProperty("temperature")
    public Double getTemperature() {
        return temperature;
    }

    @JsonProperty("temperature")
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @JsonProperty("precipitation")
    public Integer getPrecipitation() {
        return precipitation;
    }

    @JsonProperty("precipitation")
    public void setPrecipitation(Integer precipitation) {
        this.precipitation = precipitation;
    }

    @JsonProperty("globalradiation_clearsky")
    public Integer getGlobalradiationClearsky() {
        return globalradiationClearsky;
    }

    @JsonProperty("globalradiation_clearsky")
    public void setGlobalradiationClearsky(Integer globalradiationClearsky) {
        this.globalradiationClearsky = globalradiationClearsky;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("power", power)
                .append("globalradiation", globalradiation)
                .append("temperature", temperature)
                .append("precipitation", precipitation)
                .append("globalradiationClearsky", globalradiationClearsky)
                .toString();
    }
}