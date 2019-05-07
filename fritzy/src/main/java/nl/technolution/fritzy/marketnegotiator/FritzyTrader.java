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

import java.util.concurrent.TimeUnit;

import nl.technolution.DeviceId;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.market.ISupplierMarket;

/**
 * 
 */
@TimedTask(period = 1, unit = TimeUnit.MINUTES)
public final class FritzyTrader implements Runnable {

    private static final double MAX_PRICE = 25.1d;
    private final ISupplierMarket market;
    private final DeviceId deviceId;

    public FritzyTrader() {
        this.market = null;
        this.deviceId = null;
    }

    public FritzyTrader(ISupplierMarket market, DeviceId deviceId) {
        this.market = market;
        this.deviceId = deviceId;
    }

    @Override
    public void run() {
        // TODO MKE use service to get prices, no logic here
        // Instant startTradingQuarter = Instant.now().minusSeconds(1);
        // double minutes = (double)Duration.between(startTradingQuarter, Instant.now()).toMinutes();
        // double tradePrice = minutes / 15 * MAX_PRICE;
        // market.consumeOrder(deviceId.getDeviceId(), 1000, tradePrice);
    }
}
