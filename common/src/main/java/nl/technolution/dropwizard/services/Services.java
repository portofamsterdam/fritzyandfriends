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

import com.google.common.collect.MutableClassToInstanceMap;

/**
 * Helps locating services without having any context.
 */
public final class Services {

    private static final MutableClassToInstanceMap<Object> SERVICES = MutableClassToInstanceMap.create();

    private Services() {
        // Hide constructor
    }

    /**
     * Clears all registered services.
     */
    static void clearCache() {
        SERVICES.clear();
    }

    /**
     * Put entry in cache.
     * 
     * @param <T> the type of the service
     * @param serviceClass the class of the service
     * @param service the service
     * 
     */
    public static <T> void put(Class<T> serviceClass, T service) {
        SERVICES.putInstance(serviceClass, service);
    }

    /**
     * @param <T> the type of the service
     * @param serviceClass the class of the service
     * @return an instance of the service
     */
    public static <T> T get(Class<T> serviceClass) {
        T instance = SERVICES.getInstance(serviceClass);
        if (instance != null) {
            return instance;
        }
        throw new IllegalStateException("Unregistered service called " + serviceClass);
    }
}
