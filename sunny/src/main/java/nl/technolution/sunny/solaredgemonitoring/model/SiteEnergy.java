
package nl.technolution.sunny.solaredgemonitoring.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "energy" })
public class SiteEnergy {

    @JsonProperty("energy")
    private Energy energy;

    @JsonProperty("energy")
    public Energy getEnergy() {
        return energy;
    }

    @JsonProperty("energy")
    public void setEnergy(Energy energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("energy", energy).toString();
    }

}
