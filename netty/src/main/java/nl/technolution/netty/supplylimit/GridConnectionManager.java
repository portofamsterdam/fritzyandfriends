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

import java.util.Map;

import com.google.common.collect.Maps;

import nl.technolution.DeviceId;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public class GridConnectionManager implements IGridCapacityManager {

    private Map<DeviceId, Double> griConnectionLimitRegister = Maps.newHashMap();

    private double defaultGridConnectionLimit;

    @Override
    public void init(NettyConfig config) {
        defaultGridConnectionLimit = config.getDefaultGridConnectionLimit();
        config.getDeviceLimits().forEach((k, v) -> griConnectionLimitRegister.put(new DeviceId(k), v));
    }

    @Override
    public double getGridConnectionLimit(DeviceId id) {
        return griConnectionLimitRegister.computeIfAbsent(id, (k) -> defaultGridConnectionLimit);
    }
}
