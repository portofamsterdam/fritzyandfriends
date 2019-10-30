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
package nl.technolution.batty.trader;

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.FritzyApiException;

/**
 * 
 */
public class BattyTrader implements IBattyTrader {

    private final Logger log = Log.getLogger();
    private BattyResourceManager resourceManager;
    private BatteryNegotiator cem;

    @Override
    public void init(BattyConfig config) {
        resourceManager = new BattyResourceManager(new DeviceId(config.getDeviceId()));
        cem = new BatteryNegotiator(resourceManager, config);
        resourceManager.registerCustomerEnergyManager(cem);
    }

    @Override
    public void evaluateMarket() {
        try {
            cem.evaluate();
        } catch (FritzyApiException e) {
            log.error("Error during trade evaluation", e);
        }
    }

    @Override
    public void evaluateDevice() {
        resourceManager.evaluate();
    }

    @Override
    public void sendMeasurement() {
        double eDraw = Services.get(IMachineDataCacher.class).getMachineData().getEDraw();
        resourceManager.sendMeasurement(eDraw);
    }

    public BatteryNegotiator getCem() {
        return cem;
    }
}
