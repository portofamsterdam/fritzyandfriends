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

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * SolarEdge Monitoring portal value object
 */
public class Value {

    @JsonProperty("date")
    private LocalDateTime date;
    @JsonProperty("value")
    private Integer value;

    @JsonProperty("date")
    public LocalDateTime getDate() {
        return date;
    }

    @JsonProperty("date")
    // NOTE: The format of the string is like '2019-05-11 01:00:00' which is not a valid ISO dateTime string. So a
    // custom deserializer is used.
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @JsonProperty("value")
    public Integer getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("date", date).append("value", value).toString();
    }
}
