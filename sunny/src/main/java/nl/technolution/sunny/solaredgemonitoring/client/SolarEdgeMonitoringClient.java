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
package nl.technolution.sunny.solaredgemonitoring.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;

import nl.technolution.core.Log;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.solaredgemonitoring.model.Power;
import nl.technolution.sunny.solaredgemonitoring.model.SiteEnergy;
import nl.technolution.sunny.solaredgemonitoring.model.SitePower;

/**
 * Access the SolarEdge Monitoring API
 * 
 * See https://www.solaredge.com/sites/default/files/se_monitoring_api.pdf
 */
public class SolarEdgeMonitoringClient implements ISolarEdgeMonitoringClient {
    private static final Logger LOG = Log.getLogger();

    private Client client = ClientBuilder.newClient();
    private SunnyConfig config;

    @Override
    public void init(SunnyConfig config) {
        this.config = config;
        client.property(ClientProperties.CONNECT_TIMEOUT, 30000);
        client.property(ClientProperties.READ_TIMEOUT, 30000);
    }

    @Override
    public SiteEnergy getHourlyEnergy(int nrOfDays) {
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(nrOfDays);

        WebTarget target = client.target(config.getSolarEdgeMonitoringBaseURL())
                .path("energy")
                .queryParam("api_key", config.getSolarEdgeMonitoringApikey())
                .queryParam("timeUnit", "HOUR")
                .queryParam("startDate", startDate.toString())
                .queryParam("endDate", endDate.toString());

        Builder request = target.request();
        LOG.debug("Composed URL: " + target.getUri());

        Response response = request.get();
        response.bufferEntity();
        String output = response.readEntity(String.class);
        LOG.debug("Received Json:" + output);

        return response.readEntity(SiteEnergy.class);
    }

    @Override
    public Power getPower() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(15);

        // TODO WHO: better use the currentPower data from 'site overview' ??

        WebTarget target = client.target(config.getSolarEdgeMonitoringBaseURL())
                .path("power")
                .queryParam("api_key", config.getSolarEdgeMonitoringApikey())
                .queryParam("startTime", dateTimeFormatter.format(startTime))
                .queryParam("endTime", dateTimeFormatter.format(endTime));

        Builder request = target.request();
        LOG.debug("Composed URL: " + target.getUri());

        Response response = request.get();
        response.bufferEntity();
        String output = response.readEntity(String.class);
        LOG.debug("Received Json:" + output);

        return response.readEntity(SitePower.class).getPower();
    }
}
