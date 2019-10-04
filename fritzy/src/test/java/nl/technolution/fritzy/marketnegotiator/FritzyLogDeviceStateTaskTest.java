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
package nl.technolution.fritzy.marketnegotiator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.io.IIoFactory;
import nl.technolution.fritzy.io.IoFactory;
import nl.technolution.fritzy.io.tempsensor.TemperatureStub;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.ApiEvent;

/**
 * Test FritzyLogDeviceStateTask functions
 */
public class FritzyLogDeviceStateTaskTest {

    /**
     * test FritzyLogDeviceStateTask
     * 
     * @throws IOException
     * 
     */
    @Test
    public void testLogDeviceEvent() throws IOException {
        IoFactory service = new IoFactory();
        FritzyConfig testConfig = new FritzyConfig();
        testConfig.setMaxTemp(8);
        testConfig.setMinTemp(4);
        testConfig.setPower(9876);
        testConfig.setStubRelay(true);
        testConfig.setStubTemparature(true);
        testConfig.setDeviceId("Test");
        testConfig.setMarket(new MarketConfig(true, "", "test", "pwd"));

        service.init(testConfig);
        Services.put(IIoFactory.class, service);

        FritzyApiStub.reset();
        FritzyApiStub fritzyApiStub = FritzyApiStub.instance();
        Services.put(IFritzyApi.class, fritzyApiStub);

        fritzyApiStub.register("test", "test", "pwd");

        FritzyApiFactory fritzyApiFactory = new FritzyApiFactory();
        fritzyApiFactory.init(testConfig);
        Services.put(IFritzyApiFactory.class, fritzyApiFactory);

        FritzyLogDeviceStateTask task = new FritzyLogDeviceStateTask();

        boolean isCooling = false;
        double temperature = 5.678;
        ((TemperatureStub)service.getTemparatureSensor()).useFixedTemperature(temperature);
        service.getWebRelay().setRelay(isCooling);

        task.execute();
        ApiEvent stateEvent = fritzyApiStub.getFirstEventOfType(EEventType.DEVICE_STATE);
        assertEquals("State update isCooling=false,temperature=5.68", stateEvent.getMsg());
    }
}
