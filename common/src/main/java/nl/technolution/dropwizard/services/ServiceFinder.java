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
package nl.technolution.dropwizard.services;

import java.util.List;

import com.google.common.base.Preconditions;

import org.slf4j.Logger;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import nl.technolution.Log;
import nl.technolution.Services;
import nl.technolution.core.resources.TypeFinder;
import nl.technolution.dropwizard.FritzyDropWizardApp;

/**
 * 
 */
public final class ServiceFinder {

    private static final Logger LOG = Log.getLogger();

    private ServiceFinder() {
        // hide
    }

    /**
     * Setup services in a DropWizard app
     * 
     * @param configuration to inject to service
     * @param environment dropwizard
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Configuration> void setupServices(T configuration, Environment environment) {
        // Register endpoints of device controller
        LOG.info("registering services in package {}", FritzyDropWizardApp.PKG);
        List<Class<? extends IService>> services = TypeFinder
                .findImplementingClasses(FritzyDropWizardApp.PKG, IService.class);
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
}
