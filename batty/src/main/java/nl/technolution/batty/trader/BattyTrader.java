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

import nl.technolution.DeviceId;
import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.dropwizard.services.Services;

/**
 * 
 */
public class BattyTrader implements IBattyTrader {

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
        cem.evaluate();
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
