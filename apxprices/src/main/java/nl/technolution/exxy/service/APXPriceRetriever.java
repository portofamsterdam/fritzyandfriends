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

import nl.technolution.apxprices.client.PublicationMarketDocument;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.exxy.client.ITransparencyPlatformClient;

/**
 * 
 */
@TimedTask(period = 15, unit = TimeUnit.MINUTES)
public final class APXPriceRetriever implements IPriceReceiver {
    private PublicationMarketDocument cachedPrices;

    @Override
    public PublicationMarketDocument getCachedPrices() {
        return cachedPrices;
    }

    @Override
    public void execute() {
        cachedPrices = Services.get(ITransparencyPlatformClient.class).getDayAheadPrices(Instant.now());
    }
}
