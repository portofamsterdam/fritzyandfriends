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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.slf4j.Logger;

import nl.technolution.Log;

/**
 * JAX-RS exception mapping; maps exceptions them to specific HTTP error codes.
 */
@Provider
public class RSExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Log.getLogger();

    @Override
    public Response toResponse(Throwable throwable) {
        // Map Jersey, JSON and unexpected exceptions
        if (throwable instanceof WebApplicationException) {
            Response response = ((WebApplicationException)throwable).getResponse();
            String body = response.hasEntity() ? response.readEntity(String.class) : "";
            LOG.warn("Jersey exception in REST service: {}", body, throwable);
            return Response.status(response.getStatusInfo()).build();
        } else if (throwable instanceof JsonParseException || throwable instanceof JsonMappingException) {
            String msg = throwable.getMessage();
            LOG.warn("JSON parse exception in REST service", throwable);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        } else {
            LOG.error("Unhandled exception in REST service", throwable);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
