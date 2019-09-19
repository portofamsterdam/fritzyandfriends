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
package nl.technolution.fritzy.wallet.event;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.tuple.ImmutablePair;

import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.webservice.JacksonFactory;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.model.FritzyBalance;

/**
 * 
 */
public final class EventLogger {

    private final ObjectMapper mapper = JacksonFactory.defaultMapper();
    private final IFritzyApi market;

    /**
     * Constructor for {@link EventLogger} objects
     *
     * @param fritzyApi
     */
    public EventLogger(IFritzyApi fritzyApi) {
        this.market = fritzyApi;
    }

    /**
     * Log balance of a user
     * 
     * @param balance retrieve from API
     * @throws JsonProcessingException
     */
    public void logBalance(FritzyBalance balance) {
        String msg = String.format("Balance KWH: %.2f, EUR: %.2f", balance.getKwh().doubleValue(),
                balance.getEur().doubleValue());
        String data;
        try {
            data = mapper.writeValueAsString(new BalanceData(balance.getKwh(), balance.getEur()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        market.log(EEventType.BALANCE, msg, data);
    }

    /**
     * Log device state
     * 
     * @param states to include in the log message
     * @throws JsonProcessingException
     */
    public void logDeviceState(ImmutablePair<String, Object>... states) {
        String stateMsg = Arrays.asList(states)
                .stream()
                .map(s -> String.format("%s=%s", s.left, s.right.toString()))
                .collect(Collectors.joining(","));
        String msg = String.format("State update %s", stateMsg);
        String data;
        try {
            data = mapper.writeValueAsString(Arrays.asList(states));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        market.log(EEventType.DEVICE_STATE, msg, data);
    }

    /**
     * @param limit to log
     * @throws JsonProcessingException
     */
    public void logLimitActor(double limit) {
        String msg = String.format("Limit actor update limit=%.2f", limit);
        String data;
        try {
            data = mapper.writeValueAsString(new LimitData(new BigDecimal(limit)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        market.log(EEventType.LIMIT_ACTOR, msg, data);
    }

    /**
     * @param actor that exceeded threshold
     */
    public void logLimitExceeded(String actor) {
        String msg = String.format("Limit exceeded by actor %s", actor);
        String data = "{}";
        market.log(EEventType.LIMIT_EXCEEDED, msg, data);
    }

    /**
     * Log total limit
     * 
     * @param limit to show in log
     */
    public void logLimitTotal(double limit) {
        String msg = String.format("Limit total update limit=%.2f", limit);
        String data;
        try {
            data = mapper.writeValueAsString(new LimitData(new BigDecimal(limit)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        market.log(EEventType.LIMIT_TOTAL, msg, data);
    }
}
