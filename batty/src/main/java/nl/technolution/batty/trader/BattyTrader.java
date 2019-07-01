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
import nl.technolution.fritzy.wallet.FritzyApi;

/**
 * 
 */
public class BattyTrader implements IBattyTrader {

    private BattyResourceManager resourceManager;
    private BatteryNegotiator cem;

    @Override
    public void init(BattyConfig config) {
        resourceManager = new BattyResourceManager(new DeviceId(config.getDeviceId()));
        FritzyApi market = new FritzyApi(config.getMarket().getMarketUrl());
        market.login(config.getMarket().getEmail(), config.getMarket().getPassword());
        cem = new BatteryNegotiator(market, resourceManager);
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
