
package nl.technolution.sunny.pvcast.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "power" })
public class Power {

    @JsonProperty("power")
    private Integer power;

    @JsonProperty("power")
    public Integer getPower() {
        return power;
    }

    @JsonProperty("power")
    public void setPower(Integer power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("power", power).toString();
    }

}
