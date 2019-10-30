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

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.sunny.app.SunnyConfig;

/**
 * 
 */
public class SunnyTrader implements ISunnyTrader {

    private final Logger log = Log.getLogger();
    private SunnyResourceManager resourceManager;
    private SunnyNegotiator cem;

    @Override
    public void init(SunnyConfig config) {
        resourceManager = new SunnyResourceManager(new DeviceId(config.getDeviceId()));
        cem = new SunnyNegotiator(config, resourceManager);
        resourceManager.registerCustomerEnergyManager(cem);
    }

    @Override
    public void evaluateMarket() {
        try {
            cem.evaluate();
        } catch (FritzyApiException e) {
            log.error("Unable to evaluate market", e);
        }
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
