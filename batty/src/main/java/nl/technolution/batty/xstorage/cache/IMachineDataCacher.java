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
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.dropwizard.services.IService;

/**
 * 
 */
public interface IMachineDataCacher extends IService<BattyConfig> {

    /**
     * Update cached value
     */
    void update();

    /**
     * Get cached value
     * 
     * @return cached value
     */
    MachineData getMachineData();
}
