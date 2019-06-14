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
package nl.technolution.exxy.client;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;

import nl.technolution.core.Log;
import nl.technolution.exxy.app.ExxyConfig;

/**
 * Access the ENTSO-E Transparency Platform API
 * 
 * See https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html
 */
public class TransparencyPlatformClient implements ITransparencyPlatformClient {
    private static final Logger LOG = Log.getLogger();

    private Client client = JerseyClientBuilder.newClient();
    private ExxyConfig config;

    @Override
    public void init(ExxyConfig config) {
        this.config = config;
        client.property(ClientProperties.CONNECT_TIMEOUT, 30000);
        client.property(ClientProperties.READ_TIMEOUT, 30000);
    }

    @Override
    public PublicationMarketDocument getDayAheadPrices(Instant requestedDateTime) {
        // NOTE MKE: the returned data isn't accessed if fixed prices are used. dirty fix to prevent getting data anyway
        if (config.isUseFixedPrices()) {
            return null;
        }
        // Request time should be whole hours, otherwise server gives code 999 'Delivered time interval is not valid for
        // this Data item.'
        Instant start = requestedDateTime.truncatedTo(ChronoUnit.HOURS);
        WebTarget target = client.target(config.getBaseURL())
                .queryParam("securityToken", config.getSecurityToken())
                .queryParam("documentType", "A44")
                .queryParam("in_Domain", "10YNL----------L")
                .queryParam("out_Domain", "10YNL----------L")
                .queryParam("TimeInterval", start + "/" + start.plus(Duration.ofHours(1)));
        Builder request = target.request();
        LOG.debug("Composed URL: " + target.getUri());
        Response response = request.get();
        response.bufferEntity();
        String output = response.readEntity(String.class);
        if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            LOG.debug("received XML:" + output);
            return response.readEntity(PublicationMarketDocument.class);
        }
        LOG.error("Error retriving day ahead pricing data: " + response);
        return null;
    }
}
