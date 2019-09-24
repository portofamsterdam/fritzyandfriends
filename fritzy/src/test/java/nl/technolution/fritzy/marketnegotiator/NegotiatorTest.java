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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.apis.exxy.ApxPrice;
import nl.technolution.apis.exxy.IAPXPricesApi;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.io.IIoFactory;
import nl.technolution.fritzy.io.IoFactory;
import nl.technolution.fritzy.io.tempsensor.TemperatureStub;
import nl.technolution.fritzy.io.webrelay.RelayStub;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.ApiEvent;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageSystemDescription;

import io.dropwizard.jersey.params.InstantParam;

/**
 * Test negotiator
 */
public class NegotiatorTest {

    private static final String PASSWORD = "";
    private static final String FRITZY = "FRITZY_TEST";
    private static final DeviceId DEVICE_ID = new DeviceId(FRITZY);
    private FritzyNegotiator fn;
    private NettyApiImpl netty;

    private FritzyConfig config;
    private FritzyController fritzyController;

    private TemperatureStub temperatureStub;
    private RelayStub relayStub;

    @Before
    public void setup() {
        // Set locale so the string compares with balance don't fail on decimal seperator differences...
        Locale.setDefault(Locale.US);
        config = new FritzyConfig();
        config.setMarketPriceStartOffset(0.10);
        config.setMaxTemp(8);
        config.setMinTemp(4);
        config.setPower(500);
        config.setStubRelay(true);
        config.setStubTemparature(true);
        config.setDeviceId(DEVICE_ID.getDeviceId());
        config.setMarket(new MarketConfig(true, "", "", ""));

        fritzyController = new FritzyController();
        FritzyResourceManager resourceManager = new FritzyResourceManager(config, fritzyController);

        FritzyApiStub market = FritzyApiStub.instance();
        FritzyApiFactory service = new FritzyApiFactory();
        MarketConfig marketConfig = new MarketConfig(true, PASSWORD, DEVICE_ID.getDeviceId(), PASSWORD);
        config.setMarket(marketConfig);
        service.init(config);
        Services.put(IFritzyApiFactory.class, service);

        IoFactory ioFactory = new IoFactory();
        ioFactory.init(config);
        Services.put(IIoFactory.class, ioFactory);
        temperatureStub = (TemperatureStub)(ioFactory.getTemparatureSensor());
        relayStub = (RelayStub)(ioFactory.getWebRelay());

        netty = new NettyApiImpl();
        Endpoints.put(INettyApi.class, netty);
        Endpoints.put(IAPXPricesApi.class, new APXPricesApiStub());

        FritzyApiStub.reset();

        market.register(FRITZY, FRITZY, PASSWORD);
        fn = new FritzyNegotiator(config, resourceManager);
        resourceManager.registerCustomerEnergyManager(fn);
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void emptyMarket() {
        FritzyApiStub market = FritzyApiStub.instance();
        BigDecimal mintedEur = BigDecimal.valueOf(10);
        market.login(FRITZY, PASSWORD);
        market.mint(market.getAddress(), mintedEur, EContractAddress.EUR);

        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        // Evaluate the market
        fn.evaluate();
        long eventCount = market.getAllEvents()
                .stream()
                .filter(e -> e.getTag() != EEventType.DEVICE_STATE.getTag())
                .count();
        assertEquals(0, eventCount); // system description unknown, it doesn't do anything
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.evaluate();

        // check balance
        ApiEvent balanceEvent = market.getFirstEventOfType(EEventType.BALANCE);
        assertEquals("Balance KWH: 0.00, EUR: 10.00", balanceEvent.getMsg());

        // check capacity
        ApiEvent limitActorEvent = market.getFirstEventOfType(EEventType.LIMIT_ACTOR);
        assertEquals("Limit actor update limit=16.00", limitActorEvent.getMsg());

        // check created orders
        assertEquals(1, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void createOrderInEmptyMarket() {
        FritzyApiStub market = FritzyApiStub.instance();
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());

        market.login(FRITZY, PASSWORD);
        netty.rewardToGive = 2;

        fn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        // Give fritzy some money to create a buy order
        market.mint(market.getAddress(), BigDecimal.TEN, EContractAddress.EUR);
        fn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
    }

    @Test
    public void acceptExistingOrder() {
        FritzyApiStub market = FritzyApiStub.instance();
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = BigDecimal.valueOf(1);
        BigDecimal kWh = BigDecimal.valueOf(0.125d);
        market.mint(market.getAddress(), kWh, EContractAddress.KWH);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        market.login(FRITZY, PASSWORD);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        netty.rewardToGive = 2;
        fn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertTrue(netty.claimed);
    }

    @Test
    public void tooColdCanNotCool() {
        temperatureStub.useFixedTemperature(config.getMinTemp() - 1);

        FritzyApiStub market = FritzyApiStub.instance();
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());

        BigDecimal eur = BigDecimal.valueOf(10);
        market.login(FRITZY, PASSWORD);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        netty.rewardToGive = 2;
        fn.evaluate();
        assertFalse(netty.orderRewardRequested);
        assertFalse(netty.claimed);

        // no order made
        assertEquals(0, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void acceptExistingOrderNoReward() {
        FritzyApiStub market = FritzyApiStub.instance();
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = BigDecimal.valueOf(1);
        BigDecimal kWh = BigDecimal.valueOf(0.125d);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        market.login(FRITZY, PASSWORD);
        netty.rewardToGive = 0;
        fn.evaluate();
        assertTrue(netty.orderRewardRequested);
        assertFalse(netty.claimed);
    }

    @Test
    public void cancelExistingOrders() {
        FritzyApiStub market = FritzyApiStub.instance();
        market.login(FRITZY, PASSWORD);

        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());

        // Give fritzy some money to create a buy order
        market.mint(market.getAddress(), BigDecimal.TEN, EContractAddress.EUR);

        // Create order by fritzy, market is empty
        netty.rewardToGive = 1;
        fn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        // Create order by sunny for fritzy to accept
        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = BigDecimal.valueOf(0.04d);
        BigDecimal kWh = BigDecimal.valueOf(0.125d);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);
        // Give sunny some kwh to create a sell order
        market.mint(market.getAddress(), BigDecimal.TEN, EContractAddress.KWH);

        // Fritzy accepts the order from sunny
        market.login(FRITZY, PASSWORD);

        fn.evaluate();
        assertTrue(netty.claimed);
        assertTrue(netty.orderRewardRequested);
        // Existing orders by fritzy are gone, there is only 1 order left (the sunny order that is accepted by fritzy)
        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

    }

    @Test
    public void cancelExistingSunnyAcceptedFritzy() {
        FritzyApiStub market = FritzyApiStub.instance();
        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);

        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));

        market.login(FRITZY, PASSWORD);
        market.mint(market.getAddress(), BigDecimal.valueOf(10d), EContractAddress.EUR); // Give fritzy a bunch of money
        fn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        WebOrder order = market.orders().getOrders().getRecords()[0].getOrder();
        market.login(sunny, PASSWORD);
        String sunnyAddr = market.getAddress();
        market.mint(market.getAddress(), new BigDecimal(order.getTakerAssetAmount()),
                EContractAddress.getByContractName(order.getTakerAssetData()));
        market.fillOrder(order.getHash());

        market.login(FRITZY, PASSWORD);
        fn.evaluate();

        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getMakerAddress());
        assertEquals(sunnyAddr, market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);

        fn.evaluate();
        assertEquals(1, market.orders().getOrders().getRecords().length);
        // Existing orders by fritzy are gone, there is only 1 order left (the fritzy order that is accepted by sunny)
        market.login(sunny, PASSWORD);
        assertEquals(market.getAddress(), market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

    }

    @Test
    public void orderTooSmall() {
        FritzyApiStub market = FritzyApiStub.instance();
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());

        String sunny = "sunny";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = BigDecimal.valueOf(0.01);
        BigDecimal kWh = BigDecimal.valueOf((config.getPower() / 4d - 1) / 1000d);
        market.mint(market.getAddress(), kWh, EContractAddress.KWH);
        market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kWh, eur);

        market.login(FRITZY, PASSWORD);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        netty.rewardToGive = 2;
        fn.evaluate();
        assertFalse(netty.orderRewardRequested);
        assertFalse(netty.claimed);

        // Expect 2 unaccepted orders to be left (one sunny, one fritzy)
        assertEquals(2, market.orders().getOrders().getRecords().length);
        assertNull(market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
        assertNull(market.orders().getOrders().getRecords()[1].getOrder().getTakerAddress());
    }

    @Test
    public void noCoolingDueToGridLimit() {
        // set power higher than grid limit
        config.setPower(netty.getCapacity(FRITZY).getGridConnectionLimit() * 230 + 1);
        FritzyApiStub market = FritzyApiStub.instance();
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());
        fn.evaluate();
        assertFalse(netty.claimed);
        assertFalse(netty.orderRewardRequested);
        // Expect no orders
        assertEquals(0, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void buyKwhOrder() {
        FritzyApiStub market = FritzyApiStub.instance();
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());

        String sunny = "batty";
        market.register(sunny, sunny, PASSWORD);
        market.login(sunny, PASSWORD);
        BigDecimal eur = BigDecimal.valueOf(1);
        BigDecimal kWh = BigDecimal.valueOf(1);
        market.mint(market.getAddress(), kWh, EContractAddress.KWH);
        market.createOrder(EContractAddress.EUR, EContractAddress.KWH, eur, kWh);

        market.login(FRITZY, PASSWORD);
        market.mint(market.getAddress(), eur, EContractAddress.EUR);
        netty.rewardToGive = 2;
        fn.evaluate();
        assertFalse(netty.orderRewardRequested);
        assertFalse(netty.claimed);

        // Expect 2 unaccepted orders to be left (one sunny, one fritzy)
        assertEquals(2, market.orders().getOrders().getRecords().length);
        assertNull(market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());
        assertNull(market.orders().getOrders().getRecords()[1].getOrder().getTakerAddress());
    }

    @Test
    public void emergencyCooling() throws IOException {
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        // too hot but on
        temperatureStub.useFixedTemperature(config.getMaxTemp() + 1);
        relayStub.setRelay(true);
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());
        StorageInstruction instruction = (StorageInstruction)fn
                .flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        assertFalse(instruction.isIsEmergencyInstruction());
        assertEquals(1, instruction.getActuatorInstructions().getActuatorInstruction().size());
        // no previos market outcome and no emercency so
        assertEquals(EFritzyRunningMode.OFF.getRunningModeId(),
                instruction.getActuatorInstructions().getActuatorInstruction().get(0).getRunningModeId());

        // too hot but off
        temperatureStub.useFixedTemperature(config.getMaxTemp() + 1);
        relayStub.setRelay(false);
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());
        instruction = (StorageInstruction)fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        assertTrue(instruction.isIsEmergencyInstruction());
        assertEquals(1, instruction.getActuatorInstructions().getActuatorInstruction().size());
        assertEquals(EFritzyRunningMode.ON.getRunningModeId(),
                instruction.getActuatorInstructions().getActuatorInstruction().get(0).getRunningModeId());
    }

    @Test
    public void emergencyStopCooling() throws IOException {
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        // too cold but off
        temperatureStub.useFixedTemperature(config.getMinTemp() - 1);
        fn.storageSystemDescription(resourceHelper.getStorageSystemDescription());
        StorageInstruction instruction = (StorageInstruction)fn
                .flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        assertFalse(instruction.isIsEmergencyInstruction());
        assertEquals(1, instruction.getActuatorInstructions().getActuatorInstruction().size());
        assertEquals(EFritzyRunningMode.OFF.getRunningModeId(),
                instruction.getActuatorInstructions().getActuatorInstruction().get(0).getRunningModeId());

        // still too cold but on.
        relayStub.setRelay(true);
        instruction = (StorageInstruction)fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
        assertTrue(instruction.isIsEmergencyInstruction());
        assertEquals(1, instruction.getActuatorInstructions().getActuatorInstruction().size());
        assertEquals(EFritzyRunningMode.OFF.getRunningModeId(),
                instruction.getActuatorInstructions().getActuatorInstruction().get(0).getRunningModeId());
    }

    @Test
    public void noValidStorageSystemDescriptionAtFlexibilityUpdate() throws IOException {
        // re-create testobject to undo system description send from @Before
        fritzyController = new FritzyController();
        FritzyResourceManager resourceManager = new FritzyResourceManager(config, fritzyController);
        fn = new FritzyNegotiator(config, resourceManager);
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        StorageSystemDescription ssd = resourceHelper.getStorageSystemDescription();
        // set actuator id so it is invalid
        ssd.getActuatorBehaviours().getActuatorBehaviour().get(0).setActuatorId(FritzyResourceHelper.ACTUATOR_ID + 1);

        fn.storageSystemDescription(ssd);

        expectedEx.expect(IllegalStateException.class);
        fn.flexibilityUpdate(resourceHelper.getFlexibilityUpdate(fritzyController));
    }

    @Test
    public void noValidStorageSystemDescriptionAtEvaluate() throws IOException {
        // re-create testobject to undo system description send from @Before
        fritzyController = new FritzyController();
        FritzyResourceManager resourceManager = new FritzyResourceManager(config, fritzyController);
        fn = new FritzyNegotiator(config, resourceManager);
        FritzyResourceHelper resourceHelper = new FritzyResourceHelper(config);
        StorageSystemDescription ssd = resourceHelper.getStorageSystemDescription();
        // set actuator id so it is invalid
        ssd.getActuatorBehaviours().getActuatorBehaviour().get(0).setActuatorId(FritzyResourceHelper.ACTUATOR_ID + 1);

        fn.storageSystemDescription(ssd);

        expectedEx.expect(IllegalStateException.class);
        fn.evaluate();
    }

    @Test
    public void roundDetection() throws IOException {
        // TODO WHO: extract to 'logic' module (IRoundOracle and stub that so we can set the round from here?
    }

    private static class APXPricesApiStub implements IAPXPricesApi {
        @Override
        public ApxPrice getCurrentPrice() {
            return new ApxPrice(0.21d);
        }

        @Override
        public ApxPrice getNextQuarterHourPrice() {
            return new ApxPrice(0.21d);
        }

        @Override
        public ApxPrice getPrice(InstantParam requestedDateTime) {
            return new ApxPrice(0.21d);
        }
    }

    private static class NettyApiImpl implements INettyApi {

        private static final Logger LOG = Log.getLogger();
        private boolean claimed = false;
        private boolean orderRewardRequested = false;
        private double gridCapacity = 16d;
        private double groupCapacity = 32d;
        private double rewardToGive = 0d;

        @Override
        public DeviceCapacity getCapacity(String deviceId) {
            LOG.debug("getCapacity for {} returned {} {}", deviceId, gridCapacity, groupCapacity);
            return new DeviceCapacity(gridCapacity, groupCapacity);
        }

        @Override
        public void claim(String txHash, String rewardId) {
            LOG.debug("claim by {} id {}", txHash, rewardId);
            claimed = true;
        }

        @Override
        public OrderReward getOrderReward(String taker, String orderHash) {
            LOG.debug("getOrderReward by {} returns {}", taker, rewardToGive);
            orderRewardRequested = true;
            OrderReward reward = new OrderReward();
            reward.setClaimTaker(taker);
            reward.setExpireTs(LocalDateTime.MAX);
            reward.setOrderHash("0x00");
            reward.setReward(rewardToGive);
            reward.setRewardId("1");
            return reward;
        }
    }
}
