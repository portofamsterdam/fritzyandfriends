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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.google.common.collect.Maps;

import org.junit.Test;

import nl.technolution.DeviceId;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public class GridConnectionManagerTest {

    @Test
    public void testDeviceLimit() {
        double defaultGridLimit = 8.0d;
        double groupLimit = 20d;
        Map<String, Double> deviceLimits = Maps.newHashMap();
        NettyConfig config = new NettyConfig(defaultGridLimit, groupLimit, deviceLimits);
        GridConnectionManager gcm = new GridConnectionManager();
        gcm.init(config);
        DeviceId id = new DeviceId("Test");
        assertEquals(8.0d, gcm.getGridConnectionLimit(id), 0.0001d);
        assertEquals(20.0d, gcm.getGroupConnectionLimit(), 0.0001d);
        deviceLimits.put(id.getDeviceId(), 6.0d);

        config = new NettyConfig(defaultGridLimit, groupLimit, deviceLimits);
        gcm = new GridConnectionManager();
        gcm.init(config);
        assertEquals(6.0d, gcm.getGridConnectionLimit(id), 0.0001d);
        assertEquals(20.0d, gcm.getGroupConnectionLimit(), 0.0001d);
        assertEquals(8.0d, gcm.getGridConnectionLimit(new DeviceId("Test2")), 0.0001d);
    }
}
