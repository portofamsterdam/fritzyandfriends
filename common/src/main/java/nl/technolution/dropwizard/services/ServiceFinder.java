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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.base.Preconditions;

import org.slf4j.Logger;

import nl.technolution.Log;
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
     * @param initObjects to inject to service
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setupDropWizardServices(Object... initObjects) {
        // Register endpoints of device controller
        LOG.info("registering services in package {}", FritzyDropWizardApp.PKG);
        List<Class<? extends IService>> services = TypeFinder.findImplementingClasses(FritzyDropWizardApp.PKG,
                IService.class);
        LOG.info("Found {} services:", services.size());

        for (Class<? extends IService> clazz : services) {
            try {
                LOG.info("Found service: {}", clazz);

                // Find the IService interface to map with
                Class serviceInterface = null;
                for (Class<?> interfaceClazz : clazz.getInterfaces()) {
                    if (IService.class.isAssignableFrom(interfaceClazz)) {
                        serviceInterface = interfaceClazz;
                        break;
                    }
                }
                Preconditions.checkNotNull(serviceInterface);

                // Build the instance
                Class<IService<?>> typedClazz = (Class<IService<?>>)clazz;
                IService<?> service = typedClazz.newInstance();

                callInitMethod(service, initObjects);

                Services.put(serviceInterface, serviceInterface.cast(service));
            } catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static void callInitMethod(IService<?> service, Object[] objects) {
        Class<?> instanceClazz = service.getClass();
        for (Object obj : objects) {
            Class<?> calledClazz = obj.getClass();
            while (!calledClazz.equals(Object.class)) {
                try {
                    Method m = instanceClazz.getMethod("init", calledClazz);
                    LOG.debug("Calling {}", m);
                    m.invoke(service, obj);
                    break; // there can be only one
                } catch (NoSuchMethodException e) {
                    // nothing todo;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
                calledClazz = calledClazz.getSuperclass();
            }
        }
    }
}
