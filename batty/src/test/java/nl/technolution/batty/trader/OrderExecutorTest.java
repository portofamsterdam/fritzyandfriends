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
package nl.technolution.batty.trader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.DeviceId;
import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageStub;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class OrderExecutorTest {

    private final DeviceId deviceId = new DeviceId("batty");
    private final String batty = "batty@user.nl";
    private final String billy = "billy@user.nl";
    private final String password = "password";
    private final BattyResourceHelper battyResourceHelper = new BattyResourceHelper(deviceId);

    @Before
    public void setup() {

        // Prepare market
        MarketConfig marketConfig = new MarketConfig(true, password, batty, password);
        BattyConfig battyConfig = new BattyConfig();
        battyConfig.setMarket(marketConfig);
        FritzyApiFactory apiFactory = new FritzyApiFactory();
        apiFactory.init(battyConfig);
        Services.put(IFritzyApiFactory.class, apiFactory);
        
        FritzyApiStub.reset();
        FritzyApiStub apiStub = FritzyApiStub.instance();
        apiStub.register(batty, batty, password);
        apiStub.register(billy, billy, password);

        XStorageFactory xStorage = new XStorageFactory();
        battyConfig.setUseStub(true);
        xStorage.init(battyConfig);
        Services.put(IXStorageFactory.class, xStorage);
        XStorageStub.reset();
    }

    /**
     * 
     */
    @Test
    public void testExecutorOrderNotAcceptedYet() {
        // Prepare rm
        BattyResourceManager resourceManager = new BattyResourceManager(deviceId);
        StorageSystemDescription system = battyResourceHelper.getStorageSystemDescription();
        
        // Create a user
        FritzyApiStub apiStub = FritzyApiStub.instance();
        apiStub.login(batty, password);
        BigDecimal kWh = BigDecimal.valueOf(1);
        BigDecimal eur = BigDecimal.valueOf(1);
        String hash = apiStub.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        // Order not accepted yet
        OrderExecutor orderExecutor = new OrderExecutor(hash);
        assertEquals(EOrderCommand.NONE, orderExecutor.evaluate(resourceManager, system, 50d));
    }

    @Test
    public void testExecutorNoOrder() {
        BattyResourceManager resourceManager = new BattyResourceManager(deviceId);
        StorageSystemDescription system = battyResourceHelper.getStorageSystemDescription();

        OrderExecutor orderExecutor = new OrderExecutor("nonExistingHash");
        assertEquals(EOrderCommand.FINISHED, orderExecutor.evaluate(resourceManager, system, 50d));
    }

    @Test
    public void orderAcceptedDischarge() {
        BattyResourceManager resourceManager = new BattyResourceManager(deviceId);
        StorageSystemDescription system = battyResourceHelper.getStorageSystemDescription();

        FritzyApiStub apiStub = FritzyApiStub.instance();
        apiStub.login(batty, password);
        BigDecimal kWh = BigDecimal.valueOf(1);
        BigDecimal eur = BigDecimal.valueOf(1);
        apiStub.mint(apiStub.getAddress(), kWh, EContractAddress.KWH);
        String hash = apiStub.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);
        apiStub.login(billy, password);
        apiStub.mint(apiStub.getAddress(), eur, EContractAddress.EUR);
        apiStub.fillOrder(hash);
        apiStub.login(batty, password);

        OrderExecutor orderExecutor = new OrderExecutor(hash);
        assertEquals(EOrderCommand.NONE, orderExecutor.evaluate(resourceManager, system, 50d));

        Instant startTs = Efi.getNextQuarter().minusSeconds(900);
        orderExecutor.setStartTs(startTs);
        assertEquals(EOrderCommand.START, orderExecutor.evaluate(resourceManager, system, 50d));
        assertFalse(XStorageStub.instance().isCharging());
        assertTrue(XStorageStub.instance().isDischarging());
        Instant expectedEndTs = startTs.plusSeconds(720);
        assertEquals(expectedEndTs, orderExecutor.getStopExecutionTs());
    }

    @Test
    public void tooLargeOrderDischargeAccepted() {
        BattyResourceManager resourceManager = new BattyResourceManager(deviceId);
        StorageSystemDescription system = battyResourceHelper.getStorageSystemDescription();

        FritzyApiStub apiStub = FritzyApiStub.instance();
        apiStub.login(batty, password);
        BigDecimal kWh = BigDecimal.valueOf(2);
        BigDecimal eur = BigDecimal.valueOf(2);
        apiStub.mint(apiStub.getAddress(), kWh, EContractAddress.KWH);
        String hash = apiStub.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);
        apiStub.login(billy, password);
        apiStub.mint(apiStub.getAddress(), eur, EContractAddress.EUR);
        apiStub.fillOrder(hash);
        apiStub.login(batty, password);

        OrderExecutor orderExecutor = new OrderExecutor(hash);
        assertEquals(EOrderCommand.NONE, orderExecutor.evaluate(resourceManager, system, 50d));

        Instant startTs = Efi.getNextQuarter().minusSeconds(900);
        orderExecutor.setStartTs(startTs);
        assertEquals(EOrderCommand.START, orderExecutor.evaluate(resourceManager, system, 50d));
        assertFalse(XStorageStub.instance().isCharging());
        assertTrue(XStorageStub.instance().isDischarging());
        Instant expectedEndTs = startTs.plusSeconds(900);
        assertEquals(expectedEndTs, orderExecutor.getStopExecutionTs());
    }

    @Test
    public void orderAcceptedCharging() {
        BattyResourceManager resourceManager = new BattyResourceManager(deviceId);
        StorageSystemDescription system = battyResourceHelper.getStorageSystemDescription();

        FritzyApiStub apiStub = FritzyApiStub.instance();
        apiStub.login(batty, password);
        BigDecimal kWh = BigDecimal.valueOf(0.5d);
        BigDecimal eur = BigDecimal.valueOf(0.5d);
        apiStub.mint(apiStub.getAddress(), eur, EContractAddress.EUR);
        String hash = apiStub.createOrder(EContractAddress.EUR, EContractAddress.KWH, kWh, eur);
        apiStub.login(billy, password);
        apiStub.mint(apiStub.getAddress(), kWh, EContractAddress.KWH);
        apiStub.fillOrder(hash);
        apiStub.login(batty, password);

        OrderExecutor orderExecutor = new OrderExecutor(hash);
        assertEquals(EOrderCommand.NONE, orderExecutor.evaluate(resourceManager, system, 50d));

        Instant startTs = Efi.getNextQuarter().minusSeconds(900);
        orderExecutor.setStartTs(startTs);
        assertEquals(EOrderCommand.START, orderExecutor.evaluate(resourceManager, system, 75d));
        assertTrue(XStorageStub.instance().isCharging());
        assertFalse(XStorageStub.instance().isDischarging());
        Instant expectedEndTs = startTs.plusSeconds(450);
        assertEquals(expectedEndTs, orderExecutor.getStopExecutionTs());
    }

    @Test
    public void orderAcceptFromOtherCharging() {
        BattyResourceManager resourceManager = new BattyResourceManager(deviceId);
        StorageSystemDescription system = battyResourceHelper.getStorageSystemDescription();

        FritzyApiStub apiStub = FritzyApiStub.instance();
        apiStub.login(billy, password);
        BigDecimal kWh = BigDecimal.valueOf(1d);
        BigDecimal eur = BigDecimal.valueOf(1d);
        apiStub.mint(apiStub.getAddress(), kWh, EContractAddress.KWH);
        String hash = apiStub.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);
        apiStub.login(batty, password);
        apiStub.mint(apiStub.getAddress(), eur, EContractAddress.EUR);
        apiStub.fillOrder(hash);

        OrderExecutor orderExecutor = new OrderExecutor(hash);
        assertEquals(EOrderCommand.NONE, orderExecutor.evaluate(resourceManager, system, 50d));

        Instant startTs = Efi.getNextQuarter().minusSeconds(900);
        orderExecutor.setStartTs(startTs);

        assertEquals(EOrderCommand.START, orderExecutor.evaluate(resourceManager, system, 50d));
        assertTrue(XStorageStub.instance().isCharging());
        assertFalse(XStorageStub.instance().isDischarging());
        Instant expectedEndTs = startTs.plusSeconds(720);
        assertEquals(expectedEndTs, orderExecutor.getStopExecutionTs());
    }

    @Test
    public void orderAcceptFromOtherDischarging() {
        BattyResourceManager resourceManager = new BattyResourceManager(deviceId);
        StorageSystemDescription system = battyResourceHelper.getStorageSystemDescription();

        FritzyApiStub apiStub = FritzyApiStub.instance();
        apiStub.login(billy, password);
        BigDecimal kWh = BigDecimal.valueOf(1d);
        BigDecimal eur = BigDecimal.valueOf(1d);
        apiStub.mint(apiStub.getAddress(), eur, EContractAddress.EUR);
        String hash = apiStub.createOrder(EContractAddress.EUR, EContractAddress.KWH, kWh, eur);
        apiStub.login(batty, password);
        apiStub.mint(apiStub.getAddress(), kWh, EContractAddress.KWH);
        apiStub.fillOrder(hash);

        OrderExecutor orderExecutor = new OrderExecutor(hash);
        assertEquals(EOrderCommand.NONE, orderExecutor.evaluate(resourceManager, system, 50d));

        Instant startTs = Efi.getNextQuarter().minusSeconds(900);
        orderExecutor.setStartTs(startTs);

        assertEquals(EOrderCommand.START, orderExecutor.evaluate(resourceManager, system, 50d));
        assertFalse(XStorageStub.instance().isCharging());
        assertTrue(XStorageStub.instance().isDischarging());
        Instant expectedEndTs = startTs.plusSeconds(720);
        assertEquals(expectedEndTs, orderExecutor.getStopExecutionTs());

        orderExecutor.setStopExecutionTs(Instant.now().minusMillis(1));
        assertEquals(EOrderCommand.FINISHED, orderExecutor.evaluate(resourceManager, system, 50d));
        assertFalse(XStorageStub.instance().isCharging());
        assertFalse(XStorageStub.instance().isDischarging());
    }
}
