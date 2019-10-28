/*
 (C) COPYRIGHT TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.pvcast.cache;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.pvcast.client.IPvCastClient;
import nl.technolution.sunny.pvcast.model.Forecast;
import nl.technolution.sunny.pvcast.model.Forecasts;
import nl.technolution.sunny.pvcast.model.PvMeasurements;

/**
 * PvCastClient stub
 */
public class PvCastClientStub implements IPvCastClient {
    private static final Logger LOG = Log.getLogger();
    private int forcastedPower = 0;

    public void setForcastedPower(int forcastedPower) {
        this.forcastedPower = forcastedPower;
    }

    @Override
    public void init(SunnyConfig config) {
    }

    @Override
    public void postPvMeasurements(PvMeasurements pvMeasurements) {
        LOG.info("STUB: postPvMeasurements was called");
    }

    @Override
    public Forecasts getPvForecasts() throws IOException {
        Forecast forecast = new Forecast();
        forecast.setPower(forcastedPower);
        Forecasts forecasts = new Forecasts();
        Map<Long, Forecast> forecastMap = forecasts.getForecasts();
        Instant now = Instant.now();
        forecastMap.put(now.getEpochSecond(), forecast);
        forecastMap.put(now.plus(15, ChronoUnit.MINUTES).getEpochSecond(), forecast);
        forecasts.setForecasts(forecastMap);
        LOG.info("Forecasts requested from PvCast STUB");
        return forecasts;
    }
}
