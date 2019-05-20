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

import nl.technolution.batty.xstorage.connection.IXStorageConnection;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.dropwizard.services.Services;

/**
 * 
 */
class BattyController {

    void init() {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOff();
    }

    void charge() {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOn();
        connection.charge(100);
    }

    void discharge() {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOn();
        connection.discharge(0);
    }

    void stop() {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOff();
    }
}
