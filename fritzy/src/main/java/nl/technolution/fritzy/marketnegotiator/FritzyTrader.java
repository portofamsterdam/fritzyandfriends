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

import nl.technolution.fritzy.app.FritzyConfig;

/**
 * 
 */
public final class FritzyTrader implements IFritzyTrader {

    private FritzyResourceManager resourceManager;
    private FritzyNegotiator cem;

    @Override
    public void init(FritzyConfig config) {
        resourceManager = new FritzyResourceManager(config);
        cem = new FritzyNegotiator(config.getMarket(), resourceManager);
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

    public FritzyNegotiator getCem() {
        return cem;
    }
}
