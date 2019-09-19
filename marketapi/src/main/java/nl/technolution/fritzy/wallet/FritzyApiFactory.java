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
package nl.technolution.fritzy.wallet;

import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;

/**
 * 
 */
public class FritzyApiFactory implements IFritzyApiFactory {

    private FritzyAppConfig fritzyConfig;

    @Override
    public final void init(FritzyAppConfig fritzyConfig) {
        this.fritzyConfig = fritzyConfig;
    }

    /**
     * Build the market API
     * 
     * @return instance to call
     */
    @Override
    public IFritzyApi build() {
        MarketConfig marketConfig = fritzyConfig.getMarket();
        IFritzyApi api;
        if (marketConfig.isUseStub()) {
            api = FritzyApiStub.instance();
        } else {
            api = new FritzyApi(marketConfig.getMarketUrl(), fritzyConfig.getEnvironment());
            api.login(marketConfig.getEmail(), marketConfig.getPassword());
        }
        return api;
    }
}
