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
package nl.technolution.batty.xstorage;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.batty.xstorage.cache.MachineDataCache;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageStub;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.ApiEvent;

/**
 * 
 */
public class MachineDataCacheTest {

    private static final String PASSWORD = "";
    private static final String BATTY = "batty";

    @Before
    public void setup() throws InstantiationException, IllegalAccessException {

        BattyConfig config = new BattyConfig();
        FritzyApiStub market = FritzyApiStub.instance();
        FritzyApiFactory service = new FritzyApiFactory();
        MarketConfig marketConfig = new MarketConfig(true, PASSWORD, BATTY, PASSWORD);
        config.setMarket(marketConfig);
        service.init(config);
        Services.put(IFritzyApiFactory.class, service);

        XStorageFactory xStorage = new XStorageFactory();
        config.setUseStub(true);
        xStorage.init(config);
        Services.put(IXStorageFactory.class, xStorage);
        Services.put(IMachineDataCacher.class, new MachineDataCache());

        Services.put(IMachineDataCacher.class, MachineDataCache.class.newInstance());

        FritzyApiStub.reset();
        XStorageStub.reset();

        market.register(BATTY, BATTY, PASSWORD);
    }

    /**
     * 
     */
    @Test
    public void testLogging() {
        FritzyApiStub market = FritzyApiStub.instance();
        market.login(BATTY, PASSWORD);

        int sOC = 50;
        XStorageStub.instance().setStateOfCharge(sOC);

        // Call to update cache
        IMachineDataCacher machineDataCacher = Services.get(IMachineDataCacher.class);
        MachineData machineData = machineDataCacher.getMachineData();

        // check result
        List<ApiEvent> allEvents = market.getAllEvents();
        assertEquals(1, allEvents.size());
        ApiEvent apiEvent = allEvents.get(0);
        assertEquals(EEventType.DEVICE_STATE.getTag(), apiEvent.getTag());

        String exp = "[{\"isCharging\":false},{\"batteryState\":\"STANDBY\"},{\"chargeLevel\":50}]";
        assertEquals(exp, apiEvent.getData());
        assertEquals(sOC, machineData.getSoc());

        int newSoc = sOC + 10 % 100;
        XStorageStub.instance().setStateOfCharge(newSoc); // Change Soc
        machineData = machineDataCacher.getMachineData(); // Cache not updated so value is the same

        assertEquals(sOC, machineData.getSoc());
        machineDataCacher.update();
        machineData = machineDataCacher.getMachineData(); // Cache updated
        assertEquals(newSoc, machineData.getSoc());

        allEvents = market.getAllEvents();
        assertEquals(2, allEvents.size());
        apiEvent = allEvents.get(1);
        assertEquals(EEventType.DEVICE_STATE.getTag(), apiEvent.getTag());
        exp = "[{\"isCharging\":false},{\"batteryState\":\"STANDBY\"},{\"chargeLevel\":60}]";
        assertEquals(exp, apiEvent.getData());
    }
}
