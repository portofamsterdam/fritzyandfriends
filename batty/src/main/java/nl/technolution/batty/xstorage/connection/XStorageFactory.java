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
package nl.technolution.batty.xstorage.connection;

import nl.technolution.batty.app.BattyConfig;

/**
 * 
 */
public class XStorageFactory implements IXStorageFactory {

    private IXStorageConnection connection;

    @Override
    public void init(BattyConfig config) {
        if (config.isUseStub()) {
            this.connection = new XStorageStub();
            return;
        } else {
            XStorageConnection xStorageConnection = new XStorageConnection();
            xStorageConnection.init(config);
            this.connection = xStorageConnection;
        }
    }

    @Override
    public IXStorageConnection getConnection() {
        return connection;
    }

}
