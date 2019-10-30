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
package nl.technolution.exxy.trader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.exxy.app.ExxyConfig;
import nl.technolution.exxy.trader.ContinuousOrderHandler.ETrade;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.EContractAddress;

/**
 *
 */
public class ContinuousOrderHandlerTest {

    private static final String PASSWORD = "";
    private static final String EXXY = "exxy";

    @Before
    public void setup() {
        ExxyConfig config = new ExxyConfig();

        FritzyApiStub market = FritzyApiStub.instance();
        FritzyApiFactory service = new FritzyApiFactory();
        MarketConfig marketConfig = new MarketConfig(true, PASSWORD, EXXY, PASSWORD);
        config.setMarket(marketConfig);
        service.init(config);
        Services.put(IFritzyApiFactory.class, service);

        FritzyApiStub.reset();
        market.register(EXXY, EXXY, PASSWORD);
        market.login(EXXY, PASSWORD);
    }

    /**
     * @throws FritzyApiException
     * 
     */
    @Test
    public void buyTest() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();
        double kWh = 1d;
        double pricePerkWh = 0.2d;
        BigDecimal toMint = BigDecimal.valueOf(kWh * pricePerkWh);
        market.mint(market.getAddress(), toMint, EContractAddress.EUR);
        ContinuousOrderHandler orderHandler = new ContinuousOrderHandler(ETrade.BUY, kWh);
        orderHandler.changePerkWh(pricePerkWh);
        orderHandler.check();

        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(EContractAddress.KWH.getContractName(),
                market.orders().getOrders().getRecords()[0].getOrder().getTakerAssetData());
        assertEquals(market.getAddress(),
                market.orders().getOrders().getRecords()[0].getOrder().getMakerAddress());
        assertEquals(toMint.doubleValue(),
                new BigDecimal(market.orders().getOrders().getRecords()[0].getOrder().getMakerAssetAmount())
                        .doubleValue(),
                0.000001);

        orderHandler.check();
        assertEquals(1, market.orders().getOrders().getRecords().length);

        market.register("sunny", "sunny", PASSWORD);
        market.login("sunny", PASSWORD);
        String sunnyAddr = market.getAddress();
        market.mint(sunnyAddr, BigDecimal.valueOf(kWh), EContractAddress.KWH);
        market.fillOrder(orderHandler.getActiveOrderHash());

        market.login(EXXY, PASSWORD);
        assertEquals(BigDecimal.ZERO.longValue(), market.balance().getEur().longValue());
        assertEquals(BigDecimal.valueOf(kWh), market.balance().getKwh());

        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(EContractAddress.KWH.getContractName(),
                market.orders().getOrders().getRecords()[0].getOrder().getTakerAssetData());
        assertEquals(sunnyAddr, market.orders().getOrders().getRecords()[0].getOrder().getTakerAddress());

        orderHandler.check();
        assertNull(orderHandler.getActiveOrderHash()); // not enough money to create a new order
        market.mint(market.getAddress(), toMint, EContractAddress.EUR);
        orderHandler.check();
        assertEquals(2, market.orders().getOrders().getRecords().length);
    }

    @Test
    public void sellTest() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();
        double kWh = 0.1d;
        double pricePerkWh = 0.21d;
        ContinuousOrderHandler orderHandler = new ContinuousOrderHandler(ETrade.SELL, kWh);
        orderHandler.changePerkWh(pricePerkWh);
        orderHandler.check();
        assertEquals(BigDecimal.valueOf(kWh), market.balance().getKwh());
        assertEquals(1, market.orders().getOrders().getRecords().length);
        assertEquals(EContractAddress.EUR.getContractName(),
                market.orders().getOrders().getRecords()[0].getOrder().getTakerAssetData());

        market.register("fritzy", "fritzy", PASSWORD);
        market.login("fritzy", PASSWORD);
        String fritzyAddr = market.getAddress();
        market.mint(fritzyAddr, BigDecimal.valueOf(kWh * pricePerkWh), EContractAddress.EUR);
        market.fillOrder(orderHandler.getActiveOrderHash());

        market.login(EXXY, PASSWORD);
        assertEquals(BigDecimal.ZERO.longValue(), market.balance().getKwh().longValue());
        assertEquals(BigDecimal.valueOf(kWh * pricePerkWh), market.balance().getEur());

        orderHandler.check();
        assertEquals(2, market.orders().getOrders().getRecords().length);
    }

    /**
     * @throws FritzyApiException
     * 
     */
    @Test
    public void buyButBrokeTest() throws FritzyApiException {
        FritzyApiStub market = FritzyApiStub.instance();
        ContinuousOrderHandler orderHandler = new ContinuousOrderHandler(ETrade.BUY, 1d);
        orderHandler.changePerkWh(0.2d);
        orderHandler.check();
        assertEquals(0, market.orders().getOrders().getRecords().length);
    }

    /**
     * @throws FritzyApiException
     * 
     */
    @Test
    public void noPriceTest() throws FritzyApiException {
        Services.put(IFritzyApiFactory.class, new IFritzyApiFactory() {
            @Override
            public void init(FritzyAppConfig config) {
                throw new AssertionError();
            }

            @Override
            public IFritzyApi build() {
                throw new AssertionError();
            }
        });
        ContinuousOrderHandler orderHandler = new ContinuousOrderHandler(ETrade.BUY, 1d);
        orderHandler.check(); // Shouldn't do anything without a price.
    }
}
