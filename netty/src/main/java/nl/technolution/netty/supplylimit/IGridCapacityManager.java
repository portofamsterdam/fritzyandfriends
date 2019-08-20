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
package nl.technolution.netty.supplylimit;

import nl.technolution.DeviceId;
import nl.technolution.dropwizard.services.IService;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public interface IGridCapacityManager extends IService<NettyConfig> {

    /**
     * Get the supply limit for a specific device
     * 
     * @param id of device
     * @return supply limit in amps
     */
    double getGridConnectionLimit(DeviceId id);

    /**
     * Get the supply limit for the group the device is in
     * 
     * @return supply limit in amps
     */
    double getGroupConnectionLimit();
}
