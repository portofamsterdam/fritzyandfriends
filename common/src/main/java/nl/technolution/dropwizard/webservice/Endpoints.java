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

import com.google.common.collect.MutableClassToInstanceMap;

import org.slf4j.Logger;

import nl.technolution.Log;

/**
 * Register for Endpoints
 */
public final class Endpoints {

    private static final Logger LOG = Log.getLogger();

    private static final MutableClassToInstanceMap<Object> ENDPOINTS = MutableClassToInstanceMap.create();

    private Endpoints() {
        // Hide constructor
    }

    /**
     * Clears all registered services.
     */
    static void clearCache() {
        ENDPOINTS.clear();
    }

    /**
     * Put entry in cache.
     * 
     * @param <T> the type of the service
     * @param endpointClass the class of the service
     * @param endpoint the service
     * 
     */
    public static <T> void put(Class<T> endpointClass, T endpoint) {
        ENDPOINTS.putInstance(endpointClass, endpoint);
        LOG.debug("Registered endpoint {}", endpointClass.getSimpleName());
    }

    /**
     * @param <T> the type of the service
     * @param endpointClass the class of the service
     * @return an instance of the service
     */
    public static <T extends IEndpoint> T get(Class<T> endpointClass) {
        T instance = ENDPOINTS.getInstance(endpointClass);
        if (instance != null) {
            return instance;
        }
        throw new IllegalStateException("Unregistered Endpoint called " + endpointClass);
    }
}
