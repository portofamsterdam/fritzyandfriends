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

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.DeviceId;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.ApiEvent;
import nl.technolution.netty.api.NettyApi;
import nl.technolution.netty.app.NettyConfig;

/**
 * 
 */
public class GridConnectionManagerTest {

    private static final String PASSWORD = "";
    private static final String NETTY = "netty";

    private final double defaultGridLimit = 8.0d;
    private final double groupLimit = 20d;

    @Before
    public void setup() {
        // setup config
        Map<String, Double> deviceLimits = Maps.newHashMap();
        NettyConfig config = new NettyConfig(defaultGridLimit, groupLimit, deviceLimits);
        MarketConfig marketConfig = new MarketConfig(true, PASSWORD, NETTY, PASSWORD);
        config.setMarket(marketConfig);

        FritzyApiStub market = FritzyApiStub.instance();
        FritzyApiFactory service = new FritzyApiFactory();
        service.init(config);
        Services.put(IFritzyApiFactory.class, service);

        GridConnectionManager gcm = new GridConnectionManager();
        gcm.init(config);
        Services.put(IGridCapacityManager.class, gcm);

        FritzyApiStub.reset();
        market.register(NETTY, NETTY, PASSWORD);
    }

    @Test
    public void nettyApiTest() {
        NettyApi api = new NettyApi();

        // Should trigger event
        DeviceCapacity capacity = api.getCapacity(new DeviceId("batty").getDeviceId());
        assertEquals(defaultGridLimit, capacity.getGridConnectionLimit(), 0.0001d);
        assertEquals(groupLimit, capacity.getGroupConnectionLimit(), 0.0001d);

        // Check events
        FritzyApiStub market = FritzyApiStub.instance();
        List<ApiEvent> allEvents = market.getAllEvents();
        assertEquals(1, allEvents.size());

        // Validate contents of event
        ApiEvent apiEvent = allEvents.get(0);
        assertEquals(EEventType.DEVICE_STATE.getTag(), apiEvent.getTag());
        String exp = "[{\"connectionLimit\":8.0},{\"gridConnectionLimit\":20.0},{\"actor\":\"batty\"}]";
        assertEquals(exp, apiEvent.getData());
    }

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
