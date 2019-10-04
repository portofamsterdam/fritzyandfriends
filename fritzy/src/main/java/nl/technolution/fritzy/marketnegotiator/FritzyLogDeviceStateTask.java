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
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.ITaskRunner;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.fritzy.io.IIoFactory;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.EventLogger;

/**
 * 
 */
@TimedTask(period = 30, unit = TimeUnit.SECONDS)
public class FritzyLogDeviceStateTask implements ITaskRunner {
    private static final Logger LOG = Log.getLogger();

    @Override
    public void execute() {
        logDeviceState();
    }

    @SuppressWarnings("unchecked")
    private static final void logDeviceState() {
        IIoFactory ioFactory = Services.get(IIoFactory.class);
        Boolean isCooling = null;
        try {
            isCooling = ioFactory.getWebRelay().getState().isRelaystate();
        } catch (IOException e) {
            LOG.warn("Unable to read relay state", e);
        }
        double temperature = ioFactory.getTemparatureSensor().getTemparature();

        EventLogger logger = new EventLogger(Services.get(IFritzyApiFactory.class).build());
        logger.logDeviceState(new ImmutablePair<String, Object>("isCooling", isCooling),
                new ImmutablePair<String, Object>("temperature", String.format("%.2f", temperature)));

    }
}
