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
package nl.technolution.fritzy.wallet;

import javax.ws.rs.core.Response;

/**
 * 
 */
public class FritzyApiException extends Exception {

    /**
     * Constructor for {@link FritzyApiException} objects
     *
     * @param message exception message
     */
    public FritzyApiException(String message) {
        super(message);
    }

    /**
     * Check response. Calling method is reported if call fails
     * 
     * @param response to check on status 200
     * @throws FritzyApiException when call fails
     */
    public static void checkResponse(Response response) throws FritzyApiException {
        if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String methodName = stackTrace[Math.min(stackTrace.length - 1, 2)].getMethodName();
            String responsemessage = "";
            try {
                responsemessage = response.readEntity(String.class);
            } catch (IllegalStateException e) {
                responsemessage = "" + response.getStatus();
            }
            throw new FritzyApiException(methodName + " failed: " + responsemessage);
        }
    }

}
