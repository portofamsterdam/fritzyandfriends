
package nl.technolution.sunny.pvcast.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "measurements"
})
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
