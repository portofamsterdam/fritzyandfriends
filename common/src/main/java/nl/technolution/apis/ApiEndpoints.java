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
package nl.technolution.apis;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.dropwizard.webservice.IEndpoint;
import nl.technolution.dropwizard.webservice.RSCacheControlFilter;
import nl.technolution.dropwizard.webservice.RSExceptionMapper;
import nl.technolution.dropwizard.webservice.RSJacksonProvider;

/**
 * 
 */
public final class ApiEndpoints {

    /**
     * Register all API's
     * 
     * @param config
     */
    @SuppressWarnings("unchecked")
    public static void register(ApiConfig config) {
        ClientBuilder cb = ClientBuilder.newBuilder();
        for (ApiConfigRecord r : config.getApis()) {
            EApiNames apiName = EApiNames.getByName(r.getName());
            cb = cb.property(ClientProperties.CONNECT_TIMEOUT, r.getConnectTimeout())
                    .property(ClientProperties.READ_TIMEOUT, r.getReadTimeout())
                    .register(new RSJacksonFeature());
            IEndpoint ep = WebResourceFactory.newResource(apiName.getEndpointClass(), cb.build().target(r.getUrl()));
            Endpoints.put((Class<IEndpoint>)apiName.getEndpointClass(), ep);
        }
    }

    private static class RSJacksonFeature implements Feature {
        @Override
        public boolean configure(FeatureContext context) {
            Configuration config = context.getConfiguration();
            // NOTE: JacksonJsonProvider is mandatory!!! Need it for standard Jackson/JSON functionality
            if (!config.isRegistered(JacksonJsonProvider.class)) {
                context.register(JacksonJsonProvider.class);
            }
            // Disable all auto discovery features; only need stuff we configure!!!
            context.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
            context.property(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
            context.property(CommonProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
            context.property(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
            // Register from which providers which look at each other
            context.register(RSJacksonProvider.class);
            context.register(RSExceptionMapper.class);
            context.register(RSCacheControlFilter.class);
            return true;
        }
    }
}
