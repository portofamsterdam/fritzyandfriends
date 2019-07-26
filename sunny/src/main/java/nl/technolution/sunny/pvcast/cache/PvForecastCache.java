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

import org.slf4j.Logger;

import nl.technolution.core.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.pvcast.client.IPvCastClient;
import nl.technolution.sunny.pvcast.model.Forecasts;

/**
 * 
 */
public class PvForecastCache implements IPvForecastsCacher {
    private static final Logger LOG = Log.getLogger();

    private Forecasts forecasts;

    @Override
    public void init(SunnyConfig config) {
    }

    @Override
    public void update() throws IOException {
        forecasts = Services.get(IPvCastClient.class).getPvForecasts();
        LOG.info("PvForecastCache updated.");
    }

    @Override
    public Forecasts getPvForecasts() {
        if (forecasts == null) {
            try {
                update();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return forecasts;
    }
}
