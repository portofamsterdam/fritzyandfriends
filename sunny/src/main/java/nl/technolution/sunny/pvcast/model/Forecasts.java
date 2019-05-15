
package nl.technolution.sunny.pvcast.model;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Forecasts {
    // NOTE: a Treemap is used to entries are sorted, this to ease debugging
    private Map<Long, Forecast> forecasts = new TreeMap<Long, Forecast>();

    public Map<Long, Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(Map<Long, Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("forecasts", forecasts).toString();
    }
}
