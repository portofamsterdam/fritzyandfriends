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


import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.wallet.FritzyApiException;

/**
 * 
 */
public final class FritzyTrader implements IFritzyTrader {

    private final Logger log = Log.getLogger();
    private FritzyResourceManager resourceManager;
    private FritzyNegotiator cem;

    @Override
    public void init(FritzyConfig config) {
        resourceManager = new FritzyResourceManager(config, new FritzyController());
        cem = new FritzyNegotiator(config, resourceManager);
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

    public FritzyNegotiator getCem() {
        return cem;
    }
}
