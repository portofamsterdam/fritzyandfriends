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
package nl.technolution.batty.efi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.batty.xstorage.cache.MachineDataCache;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageFactory;
import nl.technolution.dropwizard.services.Services;

/**
 * Test Batty Trader functions
 */
public class BattyTraderTest {



    /**
     * 
     */
    @Test
    public void testTrading() {
        XStorageFactory service = new XStorageFactory();
        BattyConfig testConfig = new BattyConfig("Test", "", "", "", "", "", null, true);
        service.init(testConfig);
        Services.put(IXStorageFactory.class, service);
        Services.put(IMachineDataCacher.class, new MachineDataCache());
        BattyTrader trader = new BattyTrader();
        trader.init(testConfig);
        Services.put(IBattyTrader.class, trader);

        assertNull(trader.getCem().getFillLevel());
        int soc = 60;
        service.getConnection().charge(soc);
        trader.evaluateDevice();
        assertEquals((double)soc, trader.getCem().getFillLevel().doubleValue(), 0.0001d);
        soc = 80;
        service.getConnection().charge(soc);
        trader.evaluateDevice();
        assertEquals((double)soc, trader.getCem().getFillLevel().doubleValue(), 0.0001d);
        trader.sendMeasurement();
    }
}
