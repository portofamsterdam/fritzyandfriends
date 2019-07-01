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

import java.time.Instant;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Our own JAX-RS Jackson {@linkplain ObjectMapper} provider with pre'configured Jackson {@linkplain ObjectMapper}.
 * JAX-RS Jackson {@linkplain ObjectMapper} provider as is used by Jersey. It is configured so {@linkplain Instant} is
 * formatted as a ISO8601 time stamp: 2017-04-03T15:57:45.534Z. The other time types can also be used but result in
 * slightly different JSON formats.
 */
@Provider
public class RSJacksonProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    /**
     * Create new instance using given JSON converter.
     * 
     * @param objectMapper JSON converter.
     */
    public RSJacksonProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Create new instance using a default Jackson JSON converter; it includes any registered modules through libraries;
     * this done through the manifest of the linked library.
     */
    public RSJacksonProvider() {
        this(JacksonFactory.defaultMapper());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}