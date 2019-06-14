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
package nl.technolution.sunny.pvcast.client;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;

import nl.technolution.core.Log;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.pvcast.model.Forecast;
import nl.technolution.sunny.pvcast.model.Forecasts;
import nl.technolution.sunny.pvcast.model.PvMeasurements;

/**
 * Access the PVCAST API
 * 
 * See https://www.pvcast.de/api
 */
public class PvCastClient implements IPvCastClient {
    private static final Logger LOG = Log.getLogger();

    private Client client = ClientBuilder.newClient();
    private SunnyConfig config;

    @Override
    public void init(SunnyConfig config) {
        this.config = config;
        client.property(ClientProperties.CONNECT_TIMEOUT, 30000);
        client.property(ClientProperties.READ_TIMEOUT, 30000);

        // Next two lines are needed to enable request logging (+ SLF4JBridgeHandler)
        if (LOG.isDebugEnabled()) {
            client.register(new LoggingFeature(java.util.logging.Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
                    Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, 8192));
            java.util.logging.Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME).setLevel(Level.FINE);
        }
    }

    @Override
    public void postPvMeasurements(PvMeasurements pvMeasurements) {
        WebTarget target = client.target(config.getPvCastBaseURL()).path("measurements.json");
        Builder request = target.request().header("X-API-KEY", config.getPvCastApiKey());

        Entity<PvMeasurements> entity = Entity.entity(pvMeasurements, MediaType.APPLICATION_JSON);

        Response response = request.post(entity);
        // TODO WHO: handle error status
        LOG.info("Measurements sent to PvCast, result:" + response.getStatus() + " " +
                response.readEntity(String.class));
    }

    @Override
    public Forecasts getPvForecasts() throws IOException {
        WebTarget target = client.target(config.getPvCastBaseURL()).path("forecast.json");
        Builder request = target.request().header("X-API-KEY", config.getPvCastApiKey());
        Response response = request.get();
        response.bufferEntity();
        String output = response.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        Forecasts forecasts = new Forecasts();
        Map<Long, Forecast> forecastMap = forecasts.getForecasts();
        forecastMap = mapper.readValue(output, new TypeReference<Map<Long, Forecast>>() {
        });
        forecasts.setForecasts(forecastMap);
        LOG.info("Forecasts requested from PvCast, result:" + response.getStatus() + ", data:\n" + output);
        return forecasts;
    }
}
