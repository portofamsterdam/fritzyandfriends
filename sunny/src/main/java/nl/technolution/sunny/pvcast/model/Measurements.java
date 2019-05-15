
package nl.technolution.sunny.pvcast.model;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Measurements {
    // NOTE: a Treemap is used to entries are sorted, this to ease debugging
    @JsonValue
    private Map<Long, Power> measurements = new TreeMap<Long, Power>();

    public void setMeasurement(Long timestamp, Power power) {
        measurements.put(timestamp, power);
    }

    public Power getMeasurement(Long timestamp) {
        return measurements.get(timestamp);
    }

}
