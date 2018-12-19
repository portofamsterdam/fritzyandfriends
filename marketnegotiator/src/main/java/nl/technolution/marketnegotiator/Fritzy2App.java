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
package nl.technolution.marketnegotiator;

import com.fasterxml.jackson.databind.SerializationFeature;

import org.glassfish.jersey.server.validation.internal.ValidationExceptionMapper;
import org.slf4j.Logger;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import nl.technolution.IEndpoint;
import nl.technolution.appliance.IDeviceControler;
import nl.technolution.core.Log;
import nl.technolution.core.resources.TypeFinder;

/**
 * Simulator for net Power
 */

public final class Fritzy2App extends Application<AppConfiguration> {

    private static final Logger LOG = Log.getLogger();
    
    private IDeviceControler deviceControler;

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {

        environment.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        environment.jersey().register(NoCacheHandler.class);

        LOG.info("registering endpoint");
        for (Class<? extends IEndpoint> clazz : TypeFinder.findImplementingClasses("nl.technolution",
                IEndpoint.class)) {
            try {
                LOG.debug("Found endpoint: {}", clazz);
                environment.jersey().register(clazz.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
        LOG.info("registering exception mappers");
        environment.jersey().register(ValidationExceptionMapper.class);
        environment.lifecycle().manage(new MarketManager());
    }
}
