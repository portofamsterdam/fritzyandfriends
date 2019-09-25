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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

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

/**
 * Test Fritzy Trader functions
 */
public class FritzyTraderTest {

    /**
     * @throws IOException
     * 
     */
    @Test
    public void testTrading() throws IOException {
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

        FritzyTrader trader = new FritzyTrader();
        trader.init(testConfig);

        Services.put(IFritzyTrader.class, trader);

        FritzyApiStub.reset();
        FritzyApiStub fritzyApiStub = FritzyApiStub.instance();
        Services.put(IFritzyApi.class, fritzyApiStub);

        fritzyApiStub.register("test", "test", "pwd");

        FritzyApiFactory fritzyApiFactory = new FritzyApiFactory();
        fritzyApiFactory.init(testConfig);
        Services.put(IFritzyApiFactory.class, fritzyApiFactory);

        assertNull(trader.getCem().getFillLevel());

        // No accepted order so evaluateDevice is expected to turn cooling off
        boolean isCooling = false;
        double temperature = 5.6;
        ((TemperatureStub)service.getTemparatureSensor()).useFixedTemperature(temperature);
        trader.evaluateDevice();
        assertEquals(temperature, trader.getCem().getFillLevel().doubleValue(), 0.0001);
        assertEquals(isCooling, service.getWebRelay().getState().isRelaystate());
        trader.sendMeasurement();
        assertEquals(1, fritzyApiStub.getAllEvents().size());
        assertTrue(fritzyApiStub.getAllEvents().get(0).getMsg().contains("0"));

        // force emergency mode, expect cooling to be on
        isCooling = true;
        temperature = 25;
        ((TemperatureStub)service.getTemparatureSensor()).useFixedTemperature(temperature);
        // service.getWebRelay().setRelay(isCooling);
        trader.evaluateDevice();
        assertEquals(temperature, trader.getCem().getFillLevel().doubleValue(), 0.0001);
        assertEquals(isCooling, service.getWebRelay().getState().isRelaystate());
        trader.sendMeasurement();
        assertEquals(2, fritzyApiStub.getAllEvents().size());
        assertTrue(fritzyApiStub.getAllEvents().get(1).getMsg().contains("9876"));

        // too cold so evaluateMarket can be called without market stubs (will be further tested elsewhere)
        isCooling = false;
        temperature = -10;
        ((TemperatureStub)service.getTemparatureSensor()).useFixedTemperature(temperature);
        trader.evaluateDevice();
        assertEquals(temperature, trader.getCem().getFillLevel().doubleValue(), 0.0001);
        assertEquals(isCooling, service.getWebRelay().getState().isRelaystate());
        trader.evaluateMarket();
    }
}
