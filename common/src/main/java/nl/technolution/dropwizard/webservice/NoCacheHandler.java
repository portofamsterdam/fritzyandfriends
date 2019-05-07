/*
 (C) COPYRIGHT 2018 TECHNOLUTION BV, GOUDA NL
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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;

/**
 * Disables caching on the REST API.
 */
final class NoCacheHandler implements ContainerResponseFilter {
    private String cacheResponseHeader;

    NoCacheHandler() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMaxAge(0);
        cacheControl.setNoStore(true);
        cacheControl.setMustRevalidate(true);
        cacheResponseHeader = cacheControl.toString();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        responseContext.getHeaders().add(HttpHeaders.CACHE_CONTROL, cacheResponseHeader);
    }
}