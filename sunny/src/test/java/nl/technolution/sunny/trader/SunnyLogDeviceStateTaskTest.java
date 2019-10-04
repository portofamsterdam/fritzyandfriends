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
package nl.technolution.sunny.trader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.ApiEvent;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.solaredge.ISESessionFactory;
import nl.technolution.sunny.solaredge.SESessionFactory;
import nl.technolution.sunny.solaredge.SolarEdgeSessionStub;

/**
 * Test Sunny Log device state
 */
public class SunnyLogDeviceStateTaskTest {

    /**
     * Test LogDeviceEvent
     */
    @Test
    public void testLogDeviceEvent() {
        SunnyConfig testConfig = new SunnyConfig();
        testConfig.setDeviceId("Test");
        testConfig.setUseStub(true);
        testConfig.setMarket(new MarketConfig(true, "", "test", "pwd"));

        FritzyApiStub.reset();
        FritzyApiStub fritzyApiStub = FritzyApiStub.instance();
        Services.put(IFritzyApi.class, fritzyApiStub);

        fritzyApiStub.register("test", "test", "pwd");

        FritzyApiFactory fritzyApiFactory = new FritzyApiFactory();
        fritzyApiFactory.init(testConfig);
        Services.put(IFritzyApiFactory.class, fritzyApiFactory);

        SunnyLogDeviceStateTask task = new SunnyLogDeviceStateTask();

        SESessionFactory seSessionFactory = new SESessionFactory();
        seSessionFactory.init(testConfig);
        Services.put(ISESessionFactory.class, seSessionFactory);

        SolarEdgeSessionStub solarEdgeSessionStub = (SolarEdgeSessionStub)seSessionFactory.getSESession();

        solarEdgeSessionStub.setInverterPower(1234.5678);

        task.execute();
        ApiEvent stateEvent = fritzyApiStub.getFirstEventOfType(EEventType.DEVICE_STATE);
        assertEquals("State update production=1234.57", stateEvent.getMsg());
    }
}
