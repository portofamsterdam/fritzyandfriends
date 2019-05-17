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
package nl.technolution.batty.xstorage.cache;

import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.connection.IXStorageConnection;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageException;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.dropwizard.services.Services;

/**
 * 
 */
public class MachineDataCache implements IMachineDataCacher {

    private MachineData cachedSoc;

    @Override
    public void init(BattyConfig config) {
        // 
    }

    @Override
    public void update() {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        try {
            cachedSoc = connection.getMachineData();
        } catch (XStorageException ex) {
            throw new IllegalStateException("Unable to retrieve state of charge");
        }
    }

    @Override
    public MachineData getMachineData() {
        if (cachedSoc == null) {
            update();
        }
        return cachedSoc;
    }
}
