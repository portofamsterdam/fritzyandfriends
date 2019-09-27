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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.sunny.app.SunnyConfig;
import nl.technolution.sunny.solaredge.ISESessionFactory;
import nl.technolution.sunny.solaredge.SESessionFactory;
import nl.technolution.sunny.solaredge.SolarEdgeSessionStub;

/**
 * Test Sunny Trader functions
 */
public class SunnyTraderTest {

    /**
     * @throws IOException
     * 
     */
    @Test
    public void testTrading() throws IOException {
        SunnyConfig config = new SunnyConfig();
        config.setUseStub(true);
        config.setMarketPriceStartOffset(0.10);
        config.setDeviceId("Test");
        config.setMarket(new MarketConfig(true, "", "test", "pwd"));

        SunnyTrader trader = new SunnyTrader();
        trader.init(config);

        Services.put(ISunnyTrader.class, trader);

        FritzyApiStub.reset();
        FritzyApiStub fritzyApiStub = FritzyApiStub.instance();
        Services.put(IFritzyApi.class, fritzyApiStub);

        fritzyApiStub.register("test", "test", "pwd");

        FritzyApiFactory fritzyApiFactory = new FritzyApiFactory();
        fritzyApiFactory.init(config);
        Services.put(IFritzyApiFactory.class, fritzyApiFactory);

        SESessionFactory seSessionFactory = new SESessionFactory();
        seSessionFactory.init(config);
        Services.put(ISESessionFactory.class, seSessionFactory);

        SolarEdgeSessionStub solarEdgeSessionStub = (SolarEdgeSessionStub)seSessionFactory.getSESession();

        solarEdgeSessionStub.setInverterPower(12345);

        trader.sendMeasurement();
        assertEquals(1, fritzyApiStub.getAllEvents().size());
        assertTrue(fritzyApiStub.getAllEvents().get(0).getMsg().contains("12345"));
    }
}
