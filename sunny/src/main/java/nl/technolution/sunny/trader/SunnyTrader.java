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
package nl.technolution.sunny.trader;

import nl.technolution.DeviceId;
import nl.technolution.sunny.app.SunnyConfig;

/**
 * 
 */
public class SunnyTrader implements ISunnyTrader {

    private SunnyResourceManager resourceManager;
    private SunnyNegotiator cem;

    @Override
    public void init(SunnyConfig config) {
        resourceManager = new SunnyResourceManager(new DeviceId(config.getDeviceId()));
        cem = new SunnyNegotiator(config.getMarket(), resourceManager);
        resourceManager.registerCustomerEnergyManager(cem);
    }

    @Override
    public void evaluateMarket() {
        cem.evaluate();
    }

    @Override
    public void evaluateDevice() {
        resourceManager.evaluate();
    }

    @Override
    public void sendMeasurement() {
        resourceManager.sendMeasurement();
    }

    public SunnyNegotiator getCem() {
        return cem;
    }
}
