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

import java.math.BigDecimal;

import com.google.common.base.Strings;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.model.EContractAddress;

/**
 * 
 */
public final class ContinuousOrderHandler {

    private final Logger log = Log.getLogger();
    private final ETrade trade;
    private final double kwh;
    private Double pricePerkWh;

    private String activeOrderHash;

    /**
     * Constructor for {@link ContinuousOrderHandler} objects
     */
    public ContinuousOrderHandler(ETrade trade, double kwh) {
        this.kwh = kwh;
        this.trade = trade;
    }

    /**
     * Check an order
     * 
     * @throws FritzyApiException
     */
    public void check() throws FritzyApiException {
        if (pricePerkWh == null) {
            log.warn("Waiting for price");
            return;
        }

        IFritzyApi market = Services.get(IFritzyApiFactory.class).build();
        if (activeOrderHash == null) {
            activeOrderHash = createOrder(market);
            return;
        }
        WebOrder order = market.order(activeOrderHash);
        if (order == null) {
            log.error("Lost order {}", activeOrderHash);
            activeOrderHash = null; // Clear old order. New one may not be created
            activeOrderHash = createOrder(market); // recreate it
            return;
        }
        if (!Strings.isNullOrEmpty(order.getTakerAddress())) {
            log.info("{} order bought {}kWh", trade, kwh);
            activeOrderHash = null; // Clear old order. New one may not be created
            activeOrderHash = createOrder(market);
            return;
        }
    }

    private String createOrder(IFritzyApi market) throws FritzyApiException {
        BigDecimal kwhsToSell = BigDecimal.valueOf(kwh);
        BigDecimal pricePerkWhBd = kwhsToSell.multiply(BigDecimal.valueOf(pricePerkWh));
        switch (trade) {
        case BUY:
            if (market.balance().getEur().doubleValue() < kwh * pricePerkWh) {
                log.warn("Can't afford to buy energy.");
                return null;
            }
            return market.createOrder(EContractAddress.EUR, EContractAddress.KWH, pricePerkWhBd, kwhsToSell);
        case SELL:
            market.mint(market.getAddress(), kwhsToSell, EContractAddress.KWH);
            return market.createOrder(EContractAddress.KWH, EContractAddress.EUR, kwhsToSell, pricePerkWhBd);
        default:
            throw new IllegalStateException();
        }
    }


    /**
     * @param pricePerkWh
     * @throws FritzyApiException
     */
    public void changePerkWh(Double pricePerkWh) throws FritzyApiException {
        this.pricePerkWh = pricePerkWh;
        if (activeOrderHash != null) {
            Services.get(IFritzyApiFactory.class).build().cancelOrder(activeOrderHash);
        }

    }

    public String getActiveOrderHash() {
        return activeOrderHash;
    }

    public Double getPricePerkWh() {
        return pricePerkWh;
    }

    /**
     * Buy or sell
     */
    enum ETrade {
        BUY,
        SELL;
    }
}
