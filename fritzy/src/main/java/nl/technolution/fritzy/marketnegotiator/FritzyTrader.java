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

import nl.technolution.DeviceId;
import nl.technolution.fritzy.app.FritzyConfig;

/**
 * 
 */
public final class FritzyTrader implements IFritzyTrader {

    private FritzyResourceManager resourceManager;
    private FritzyNegotiator cem;

    @Override
    public void init(FritzyConfig config) {
        resourceManager = new FritzyResourceManager(new DeviceId(config.getDeviceId()));
        cem = new FritzyNegotiator(config.getMarket(), resourceManager);
        resourceManager.registerCustomerEnergyManager(cem);

    }

    @Override
    public void evaluateMarket() {
        //

    }

    @Override
    public void evaluateDevice() {
        //

    }

    @Override
    public void sendMeasurement() {
        //

    }
}
