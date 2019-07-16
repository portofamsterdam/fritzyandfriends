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

/**
 * 
 */
public class FritzyApiFactoryStub implements IFritzyApiFactory {

    private IFritzyApi api;

    @Override
    public void init(FritzyAppConfig config) {
    }

    @Override
    public IFritzyApi build() {
        if (api != null) {
            return api;
        }
        synchronized (FritzyApiFactoryStub.class) {
            if (api == null) {
                api = new FritzyApiStub();
            }
            return api;
        }
    }
}
