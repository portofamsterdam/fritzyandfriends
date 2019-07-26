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
package nl.technolution.fritzy.marketnegotiator;

import java.io.IOException;

import org.slf4j.Logger;

import nl.technolution.core.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.io.IIoFactory;

/**
 * 
 */
public class FritzyController {
    private static final Logger LOG = Log.getLogger();

    /**
     * Power on Fritzy
     */
    public void on() {
        try {
            Services.get(IIoFactory.class).getWebRelay().setRelay(true);
        } catch (IOException e) {
            LOG.error("Error powering on fritzy:", e);
        }
    }

    /**
     * Power off Fritzy
     */
    public void off() {
        try {
            Services.get(IIoFactory.class).getWebRelay().setRelay(false);
        } catch (IOException e) {
            LOG.error("Error powering off fritzy:", e);
        }
    }
}
