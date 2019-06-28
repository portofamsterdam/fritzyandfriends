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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Helps generating pre'configured {@linkplain ObjectMapper}.
 */
public final class JacksonFactory {

    private JacksonFactory() {
        // Hide constructor
    }

    /**
     * Returns a 'JSON' {@link ObjectMapper} with type support for Guava (e.g. Immutable...), Java Time and JDK8
     * type (e.g. Optional). Preferred Java Time type is {@link Instant} which maps onto ISO8601 format.
     * 
     * @return {@link ObjectMapper} instance.
     */
    public static ObjectMapper defaultMapper() {
        // NOTE: one can use findRegisteredModules but explicit registration gives us more control
        ObjectMapper objectMapper = new ObjectMapper();
        // NOTE: don't add JAXB support here; if you want that, define a new factory method.
        objectMapper.registerModule(new GuavaModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        // Configure it for readable date/time stamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
