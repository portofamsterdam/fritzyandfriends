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
package nl.technolution.exxy.service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.exxy.client.ITransparencyPlatformClient;
import nl.technolution.exxy.client.PublicationMarketDocument;

/**
 * 
 */
@TimedTask(period = 15, unit = TimeUnit.MINUTES)
public final class APXPriceRetriever implements IPriceReceiver {
    private static final Logger LOG = Log.getLogger();
    private PublicationMarketDocument cachedPrices;

    @Override
    public PublicationMarketDocument getCachedPrices() {
        return cachedPrices;
    }

    @Override
    public void execute() {
        cachedPrices = Services.get(ITransparencyPlatformClient.class).getDayAheadPrices(Instant.now());
        if (cachedPrices != null) {
            LOG.info("New prices retrived, cache now conatains prices from {} till {}",
                    cachedPrices.getPeriodTimeInterval().getStart(), cachedPrices.getPeriodTimeInterval().getEnd());
        }
    }
}
