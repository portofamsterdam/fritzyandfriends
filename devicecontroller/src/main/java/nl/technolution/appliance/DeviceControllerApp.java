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
package nl.technolution.appliance;

import java.util.List;

import com.fasterxml.jackson.databind.SerializationFeature;

import org.glassfish.jersey.server.validation.internal.ValidationExceptionMapper;
import org.slf4j.Logger;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import nl.technolution.IEndpoint;
import nl.technolution.core.Log;
import nl.technolution.core.resources.TypeFinder;

/**
 * Simulator for net Power
 * 
 * @param <T> Configuration
 */
public abstract class DeviceControllerApp<T extends Configuration> extends Application<T> implements IDevice {

    private static final Logger LOG = Log.getLogger();
    
    @Override
    public final void run(T configuration, Environment environment) throws Exception {

        LOG.info("Setup market manager");
        initEnvironment(environment, configuration);

        LOG.info("Setup webservices");
        setupWebservices(environment);

        LOG.info("Setup device");
        initDevice(configuration);
    }

    /**
     * Device initialised
     * 
     * @param configuration
     */
    protected abstract void initEnvironment(Environment environment, T configuration);

    /**
     * Device initialised
     * 
     * @param configuration
     */
    protected abstract void initDevice(T configuration);

    private void setupWebservices(Environment environment) {

        // Register some defaults
        environment.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        environment.jersey().register(NoCacheHandler.class);
        environment.jersey().register(ValidationExceptionMapper.class);

        // Register endpoints of device controller
        String pkg = "nl.technolution";
        LOG.info("registering package {}", pkg);
        List<Class<? extends IEndpoint>> endpoints = TypeFinder.findImplementingClasses(pkg, IEndpoint.class);
        LOG.info("Found {} endpoints:", endpoints.size());
        endpoints.forEach(c -> LOG.info("{}", c.getSimpleName()));
        for (Class<? extends IEndpoint> clazz : endpoints) {
            try {
                LOG.debug("Found endpoint: {}", clazz);
                environment.jersey().register(clazz.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
    }


}
