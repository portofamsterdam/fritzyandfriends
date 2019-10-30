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

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.exxy.app.ExxyConfig;
import nl.technolution.exxy.service.APXPricesService;
import nl.technolution.exxy.service.IAPXPricesService;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.FritzyApiFactory;
import nl.technolution.fritzy.wallet.FritzyApiStub;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.EContractAddress;

/**
 * 
 */
public class ExxyTraderTest {

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

        config.setOrderSizes(Lists.newArrayList(1d));
        ExxyTrader trader = new ExxyTrader();
        trader.init(config);
        Services.put(IExxyTrader.class, trader);

        config.setUseFixedPrices(true);
        Map<Integer, Double> fixedPrices = Maps.newHashMap();
        IntStream.range(0, 24).forEach(i -> fixedPrices.put(i, 0.021d));
        config.setFixedPrices(fixedPrices);
        IAPXPricesService apxPrices = new APXPricesService();
        apxPrices.init(config);
        Services.put(IAPXPricesService.class, apxPrices);

        FritzyApiStub.reset();
        market.register(EXXY, EXXY, PASSWORD);
        market.login(EXXY, PASSWORD);

    }

    /**
     * Test Exxy trading
     * 
     * @throws FritzyApiException
     */
    @Test
    public void exxyTraderTest() throws FritzyApiException {
        ExxyTraderTask exxyTraderTask = new ExxyTraderTask();
        exxyTraderTask.execute();
        IFritzyApi market = Services.get(IFritzyApiFactory.class).build();
        assertEquals(1, market.orders().getOrders().getRecords().length);
        market.mint(market.getAddress(), BigDecimal.TEN, EContractAddress.EUR);
        exxyTraderTask.execute();
        assertEquals(2, market.orders().getOrders().getRecords().length);
    }
}
