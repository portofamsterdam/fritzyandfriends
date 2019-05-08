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
package nl.technolution.fritzy.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import nl.technolution.market.MarketConfig;

/**
 * Configuration for Fritzy
 */
public class FritzyConfig extends Configuration {

    @JsonProperty
    private final String deviceId;

    /** host address of webrelay */
    @JsonProperty
    private final String host;

    /** Port of webrelay (default 80) */
    @JsonProperty
    private final int port;

    /** Port used to read temp sensor */
    @JsonProperty
    private final String serialPort;

    @JsonProperty
    private final boolean stubTemparature;

    @JsonProperty
    private final boolean stubRelay;

    @JsonProperty
    private final MarketConfig market;

    @JsonCreator
    public FritzyConfig(@JsonProperty String deviceId,
            @JsonProperty String host,
            @JsonProperty int port,
            @JsonProperty String serialPort,
            @JsonProperty boolean stubTemparature,
            @JsonProperty boolean stubRelay,
            @JsonProperty MarketConfig market) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.serialPort = serialPort;
        this.stubTemparature = stubTemparature;
        this.stubRelay = stubRelay;
        this.market = market;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public MarketConfig getMarket() {
        return market;
    }

    public String getSerialPort() {
        return serialPort;
    }

    public boolean isStubTemparature() {
        return stubTemparature;
    }

    public boolean isStubRelay() {
        return stubRelay;
    }
}

