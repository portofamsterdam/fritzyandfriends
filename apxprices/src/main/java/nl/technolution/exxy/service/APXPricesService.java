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
package nl.technolution.exxy.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Map;

import org.slf4j.Logger;

import eu.entsoe.wgedi.codelists.StandardUnitOfMeasureTypeList;
import nl.technolution.apxprices.client.Point;
import nl.technolution.apxprices.client.PublicationMarketDocument;
import nl.technolution.apxprices.client.SeriesPeriod;
import nl.technolution.apxprices.client.TimeSeries;
import nl.technolution.core.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.exxy.app.ExxyConfig;
import nl.technolution.exxy.client.ITransparencyPlatformClient;

/**
 * APXPricesService
 */
public class APXPricesService implements IAPXPricesService {
    private static final Logger LOG = Log.getLogger();
    private static final DateTimeFormatter DATE_TIME_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'")
            .withZone(ZoneId.of("UTC"));
    private Map<Integer, Double> fixedPrices;
    private boolean useFixedPrices;

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
    public void init(ExxyConfig config) {
        fixedPrices = config.getFixedPrices();
        useFixedPrices = config.isUseFixedPrices();
    }

    @Override
    public double getPricePerkWh() throws NoPricesAvailableException {
        if (useFixedPrices) {
            return getFixedPrice(Instant.now());
        }
        IPriceReceiver priceReceiver = Services.get(IPriceReceiver.class);
        PublicationMarketDocument cachedPrices = priceReceiver.getCachedPrices();
        if (cachedPrices == null) {
            throw new NoPricesAvailableException("No prices available yet.");
        }
        return getSinglePrice(Instant.now(), cachedPrices);
    }

    @Override
    public double getPricePerkWh(Instant requestedDateTime) throws NoPricesAvailableException {
        if (useFixedPrices) {
            return getFixedPrice(requestedDateTime);
        }
        ITransparencyPlatformClient transparencyPlatformClient = Services.get(ITransparencyPlatformClient.class);
        return getSinglePrice(requestedDateTime, transparencyPlatformClient.getDayAheadPrices(requestedDateTime));
    }

    private double getFixedPrice(Instant requestedDateTime) {
        int hourOfDay = LocalDateTime.ofInstant(requestedDateTime, ZoneOffset.systemDefault())
                .toLocalTime()
                .get(ChronoField.HOUR_OF_DAY);
        double price = fixedPrices.get(hourOfDay);
        LOG.info("Using FIXED price {} as set for {}:00", price, hourOfDay);
        return price;
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
                            LOG.info("For requested time {} found pricepoint at position {}: value {} {} per {}",
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