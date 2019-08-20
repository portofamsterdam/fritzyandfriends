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

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.core.Log;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public class GridConnectionManager implements IGridCapacityManager {

    private final Logger LOG = Log.getLogger();

    private Map<DeviceId, Double> griConnectionLimitRegister = Maps.newHashMap();

    private double defaultGridConnectionLimit;
    private double groupConnectionLimit;

    @Override
    public void init(NettyConfig config) {
        defaultGridConnectionLimit = config.getDefaultGridConnectionLimit();
        groupConnectionLimit = config.getGroupConnectionLimit();
        config.getDeviceLimits().forEach((k, v) -> griConnectionLimitRegister.put(new DeviceId(k), v));
    }

    @Override
    public double getGridConnectionLimit(DeviceId id) {
        return griConnectionLimitRegister.computeIfAbsent(id, (k) -> defaultGridConnectionLimit);
    }

    @Override
    public double getGroupConnectionLimit() {
        return groupConnectionLimit;
    }
}
