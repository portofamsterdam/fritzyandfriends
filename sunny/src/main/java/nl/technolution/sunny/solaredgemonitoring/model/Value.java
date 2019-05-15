
package nl.technolution.sunny.solaredgemonitoring.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "date", "value" })
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
    // NOTE WHO: The format of the string is like '2019-05-11 01:00:00' which is not a valid ISO dateTime string. So a
    // custom deserializer is used.
    // TODO WHO: Check with Martin if there is a way to specify the format here (like this
    // http://www.adam-bien.com/roller/abien/entry/serializing_and_deserializing_a_pojo
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
