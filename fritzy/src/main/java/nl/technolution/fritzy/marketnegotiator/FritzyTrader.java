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

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.technolution.TimedTaskService;
import nl.technolution.market.ISupplierMarket;
import nl.technolution.protocols.efi.util.DeviceId;

/**
 * 
 */
public final class FritzyTrader extends TimedTaskService {

    private static final double MAX_PRICE = 25.1d;
    private final ISupplierMarket market;
    private final DeviceId deviceId;

    public FritzyTrader(ISupplierMarket market, DeviceId deviceId) {
        this.market = market;
        this.deviceId = deviceId;
    }

    @Override
    public void init(ScheduledExecutorService executor) {
        executor.scheduleAtFixedRate(this::updatePrices, 0, 1, TimeUnit.MINUTES);
    }

    private void updatePrices() {
        Instant startTradingQuarter = Instant.now().minusSeconds(1);
        double minutes = (double)Duration.between(startTradingQuarter, Instant.now()).toMinutes();
        double tradePrice = minutes / 15 * MAX_PRICE;
        market.consumeOrder(deviceId.getDeviceId(), 1000, tradePrice);
    }
}
