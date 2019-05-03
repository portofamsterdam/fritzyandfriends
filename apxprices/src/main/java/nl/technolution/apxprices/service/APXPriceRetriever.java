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
package nl.technolution.apxprices.service;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.technolution.TimedTaskService;
import nl.technolution.apxprices.client.ITransparencyPlatformClient;
import nl.technolution.apxprices.client.PublicationMarketDocument;

/**
 * 
 */
public final class APXPriceRetriever extends TimedTaskService {
    private ITransparencyPlatformClient client;
    private PublicationMarketDocument cachedPrices;

	public APXPriceRetriever(ITransparencyPlatformClient client) {
        this.client = client;
	}
	
    @Override
    public void init(ScheduledExecutorService executor) {
        executor.scheduleAtFixedRate(this::updatePrices, 0, 15, TimeUnit.MINUTES);
    }

    private void updatePrices() {
    	cachedPrices = client.getDayAheadPrices(Instant.now());
    }
    
    public PublicationMarketDocument getCachedPrices() {
    	return cachedPrices;
    }
}
