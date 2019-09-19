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

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

/**
 * Cache control filter; is now global for all services which is good for simple low impact REST services.<br>
 * NOTE: for fine tuned cache control another filter implementation is needed using preferably using
 * annotations on methods.
 */
@Priority(Priorities.HEADER_DECORATOR)
@Provider
public class RSCacheControlFilter implements ContainerResponseFilter {

    private final String cacheControlValue;

    /**
     * Constructor
     *
     * @param cacheControl Cache control info.
     */
    public RSCacheControlFilter(CacheControl cacheControl) {
        cacheControlValue = cacheControl.toString();
    }

    /**
     * Constructor
     */
    public RSCacheControlFilter() {
        this(disableCacheControl());
    }

    /**
     * @return 'disable' cache control.
     */
    public static CacheControl disableCacheControl() {
        CacheControl result = new CacheControl();
        // Based on set once found for Jetty
        result.setNoCache(true); // don't cache data.
        result.setNoStore(true); // don't store security sensitive info
        result.setMustRevalidate(true);
        result.setMaxAge(0);
        return result;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        // Only override cache control in case of 200 code; NOTE: code copied from 'net', not validated!
        if (responseContext.getStatus() == 200) {
            responseContext.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, cacheControlValue);
        }
    }
}