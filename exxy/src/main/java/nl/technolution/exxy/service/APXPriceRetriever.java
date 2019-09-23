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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.exxy.client.ITransparencyPlatformClient;
import nl.technolution.exxy.client.PublicationMarketDocument;
import nl.technolution.exxy.service.APXPricesService.NoPricesAvailableException;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.EventLogger;
import nl.technolution.protocols.efi.util.Efi;

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
            logDeviceState(cachedPrices);
        }
    }

    @SuppressWarnings("unchecked")
    private static void logDeviceState(PublicationMarketDocument cachedPrices) {
        EventLogger logger = new EventLogger(Services.get(IFritzyApiFactory.class).build());
        try {
            double price = APXPricesService.getSinglePrice(Efi.getNextQuarter(), cachedPrices);
            logger.logDeviceState(new ImmutablePair<String, Object>("price", price));
        } catch (NoPricesAvailableException ex) {
            LOG.error("Unable to log price", ex);
        }
    }
}
