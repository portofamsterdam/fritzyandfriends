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
package nl.technolution.dropwizard;

import java.util.List;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Preconditions;

import org.glassfish.jersey.server.validation.internal.ValidationExceptionMapper;
import org.slf4j.Logger;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import nl.technolution.Services;
import nl.technolution.core.Log;
import nl.technolution.core.resources.TypeFinder;

/**
 * Basic app for Fritzy applications
 * 
 * @param <T> Dropwizard Configuration Type
 */
public class FritzyDropWizardApp<T extends Configuration> extends Application<T> {

    private static final Logger LOG = Log.getLogger();
    private static final String PKG = "nl.technolution";

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        setupServices(configuration, environment);
        setupWebservices(environment);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setupServices(T configuration, Environment environment) {
        // Register endpoints of device controller
        LOG.info("registering services in package {}", PKG);
        List<Class<? extends IService>> services = TypeFinder.findImplementingClasses(PKG, IService.class);
        LOG.info("Found {} services:", services.size());

        for (Class<? extends IService> clazz : services) {
            try {
                LOG.info("Found service: {}", clazz);

                // Find the IService interface to map with
                Class serviceInterface = null;
                for (Class<?> interfaceClazz : clazz.getInterfaces()) {
                    if (IService.class.isAssignableFrom(interfaceClazz)) {
                        serviceInterface = interfaceClazz;
                    }
                }
                Preconditions.checkNotNull(serviceInterface);

                // Build the instance
                Class<IService<T>> typedClazz = (Class<IService<T>>)clazz;
                IService<T> service = typedClazz.newInstance();
                service.init(configuration);

                Services.put(serviceInterface, serviceInterface.cast(service));
            } catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void setupWebservices(Environment environment) {

        // Register some defaults
        environment.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        environment.jersey().register(ValidationExceptionMapper.class);
        environment.jersey().register(NoCacheHandler.class);

        // Register endpoints of device controller
        String pkg = "nl.technolution";
        LOG.info("registering endpoints in package {}", pkg);
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
