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
package nl.technolution.dropwizard.webservice;

import java.util.List;

import com.fasterxml.jackson.databind.SerializationFeature;

import org.glassfish.jersey.server.validation.internal.ValidationExceptionMapper;
import org.slf4j.Logger;

import io.dropwizard.setup.Environment;
import nl.technolution.Log;
import nl.technolution.core.resources.TypeFinder;

/**
 * Find a register webservices to Dropwizard app
 */
public final class WebserviceFinder {

    private WebserviceFinder() {
        // hide
    }

    /**
     * @param environment to register webservices in
     */
    public static void setupWebservices(Environment environment) {
        Logger log = Log.getLogger();
        // Register some defaults
        environment.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        environment.jersey().register(ValidationExceptionMapper.class);
        environment.jersey().register(NoCacheHandler.class);

        // Register endpoints of device controller
        String pkg = "nl.technolution";
        log.info("registering endpoints in package {}", pkg);
        List<Class<? extends IEndpoint>> endpoints = TypeFinder.findImplementingClasses(pkg, IEndpoint.class);
        log.info("Found {} endpoints:", endpoints.size());
        endpoints.forEach(c -> log.info("{}", c.getSimpleName()));
        for (Class<? extends IEndpoint> clazz : endpoints) {
            try {
                log.debug("Found endpoint: {}", clazz);
                environment.jersey().register(clazz.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
