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
     * @param initObjects to inject to service
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setupDropWizardServices(Object... initObjects) {
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
            try {
                Method m = instanceClazz.getMethod(getInitMethod(), obj.getClass());
                LOG.info("Invoking init method {} with {}", m, obj.getClass());
                m.invoke(service, obj);
            } catch (NoSuchMethodException e) {
                continue;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private static String getInitMethod() {
        Method[] methods = IService.class.getMethods();
        Preconditions.checkArgument(methods.length == 1, "IService should only have one method init");
        return methods[0].getName();
    }
}
