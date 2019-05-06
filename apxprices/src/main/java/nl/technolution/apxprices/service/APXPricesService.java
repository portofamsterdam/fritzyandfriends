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
package nl.technolution.apxprices.service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;

import nl.technolution.apxprices.app.APXPricesConfig;
import nl.technolution.apxprices.client.Point;
import nl.technolution.apxprices.client.PublicationMarketDocument;
import nl.technolution.apxprices.client.SeriesPeriod;
import nl.technolution.apxprices.client.TimeSeries;
import nl.technolution.apxprices.client.TransparencyPlatformClient;
import nl.technolution.core.Log;

import eu.entsoe.wgedi.codelists.StandardUnitOfMeasureTypeList;

/**
 * APXPricesService
 */
public class APXPricesService implements IAPXPricesService {
    private static final Logger LOG = Log.getLogger();
    private static final DateTimeFormatter DATE_TIME_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'")
            .withZone(ZoneId.of("UTC"));
    private APXPriceRetriever apxPriceRetriever;
    private TransparencyPlatformClient client;

    /**
     * Custom exception
     */
    public static class NoPricesAvailableException extends Exception {
        private static final long serialVersionUID = 1L;

        public NoPricesAvailableException(String msg) {
            super(msg);
        }
    }

    @Override
    public void init(APXPricesConfig config) {
        client = new TransparencyPlatformClient(config);
        apxPriceRetriever = new APXPriceRetriever(client);
    }

    @Override
    public double getPricePerkWh() throws NoPricesAvailableException {
        PublicationMarketDocument cachedPrices = apxPriceRetriever.getCachedPrices();
        if (apxPriceRetriever.getCachedPrices() == null) {
            throw new NoPricesAvailableException("No prices available yet.");
        }
        return getSinglePrice(Instant.now(), cachedPrices);
    }

    @Override
    public double getPricePerkWh(Instant requestedDateTime) throws NoPricesAvailableException {
        return getSinglePrice(requestedDateTime, client.getDayAheadPrices(requestedDateTime));
    }

    private double getSinglePrice(Instant requestedDateTime, PublicationMarketDocument prices)
            throws NoPricesAvailableException {
        for (TimeSeries timeSeries : prices.getTimeSeries()) {
            LOG.debug("Processing timeseries with mRID=" + timeSeries.getMRID());
            long factor;
            StandardUnitOfMeasureTypeList unit = StandardUnitOfMeasureTypeList
                    .fromValue(timeSeries.getPriceMeasureUnitName());
            switch (unit) {
            case GWH:
                factor = 1000 * 1000;
                break;
            case KWH:
                factor = 1;
                break;
            case MWH:
                factor = 1000;
                break;
            default:
                throw new RuntimeException("Factor " + unit + " not expected.");
            }
            LOG.debug("Factor for unit " + unit + " is " + factor);
            for (SeriesPeriod period : timeSeries.getPeriod()) {
                Instant periodStart = Instant.from(DATE_TIME_PARSER.parse(period.getTimeInterval().getStart()));
                Instant periodEnd = Instant.from(DATE_TIME_PARSER.parse(period.getTimeInterval().getEnd()));
                LOG.debug("Processing period " + periodStart + "-" + periodEnd);
                if (!requestedDateTime.isBefore(periodStart) && requestedDateTime.isBefore(periodEnd)) {
                    LOG.debug("requestedDatetime {} is in current period {}-{}", requestedDateTime, periodStart,
                            periodEnd);
                    // calc point we need based on interval and current time
                    // first convert javax.xml.datatype.Duration to Duration
                    Duration resolution = Duration.parse(period.getResolution().toString());
                    long offset = Duration.between(periodStart, requestedDateTime).getSeconds() /
                            resolution.getSeconds();
                    for (Point point : period.getPoint()) {
                        if (point.getPosition() == offset + 1) {
                            LOG.debug("For requested time {} found pricepoint at position {}: value {} {} per {}",
                                    requestedDateTime, point.getPosition(), point.getPriceAmount().doubleValue(),
                                    timeSeries.getCurrencyUnitName(), unit);
                            return point.getPriceAmount().doubleValue() / factor;
                        }
                    }
                }
            }
        }
        throw new NoPricesAvailableException("No price for " + requestedDateTime + " found in available price data.");
    }
}