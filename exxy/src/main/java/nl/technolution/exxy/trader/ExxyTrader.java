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

import java.time.Instant;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.exxy.app.ExxyConfig;
import nl.technolution.exxy.service.APXPricesService.NoPricesAvailableException;
import nl.technolution.exxy.service.IAPXPricesService;
import nl.technolution.exxy.trader.ContinuousOrderHandler.ETrade;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class ExxyTrader implements IExxyTrader {

    private final Logger log = Log.getLogger();

    private List<ContinuousOrderHandler> orders = Lists.newArrayList();
    private Double pricePerkWh;
    private Instant nextPriceTs = Instant.MIN;

    @Override
    public void init(ExxyConfig config) {
        for (Double kWh : config.getOrderSizes()) {
            orders.add(new ContinuousOrderHandler(ETrade.BUY, kWh));
            orders.add(new ContinuousOrderHandler(ETrade.SELL, kWh));
        }
    }

    @Override
    public void evaluateMarket() {
        try {
            checkNextPrice();
        } catch (FritzyApiException e) {
            log.error("Unable to push price change to market", e);
        }
    }

    private void checkNextPrice() throws FritzyApiException {
        if (nextPriceTs.isBefore(Instant.now())) {
            IAPXPricesService pricesService = Services.get(IAPXPricesService.class);
            try {
                pricePerkWh = pricesService.getPricePerkWh();
                for (ContinuousOrderHandler orderHandler : orders) {
                    orderHandler.changePerkWh(pricePerkWh);
                }
                nextPriceTs = Efi.getNextQuarter();
            } catch (NoPricesAvailableException e) {
                log.error("Unable to get apx price information. Can't trade");
                return;
            }
        }
        Preconditions.checkNotNull(pricePerkWh);
        for (ContinuousOrderHandler orderHandler: orders) {
            orderHandler.check();
        }
    }
}
