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
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Forecasts object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "power", "globalradiation", "temperature", "precipitation", "globalradiation_clearsky" })
public class Forecast {
    private Integer power;
    private Integer globalradiation;
    private Double temperature;
    private Integer precipitation;
    private Integer globalradiationClearsky;

    @JsonProperty("power")
    @JsonPropertyDescription("power in watts. If there is no forecast available yet, the power key and its value are " +
            "missing.")
    public Integer getPower() {
        return power;
    }

    @JsonProperty("power")
    public void setPower(Integer power) {
        this.power = power;
    }

    @JsonProperty("globalradiation")
    @JsonPropertyDescription("globalradiation in watts")
    public Integer getGlobalradiation() {
        return globalradiation;
    }

    @JsonProperty("globalradiation")
    public void setGlobalradiation(Integer globalradiation) {
        this.globalradiation = globalradiation;
    }

    @JsonProperty("temperature")
    @JsonPropertyDescription("Real Number: temperature in degrees C")
    public Double getTemperature() {
        return temperature;
    }

    @JsonProperty("temperature")
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @JsonProperty("precipitation")
    @JsonPropertyDescription("precipitation in mm")
    public Integer getPrecipitation() {
        return precipitation;
    }

    @JsonProperty("precipitation")
    public void setPrecipitation(Integer precipitation) {
        this.precipitation = precipitation;
    }

    @JsonProperty("globalradiation_clearsky")
    @JsonPropertyDescription("globalradiation under clear sky in watts")
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