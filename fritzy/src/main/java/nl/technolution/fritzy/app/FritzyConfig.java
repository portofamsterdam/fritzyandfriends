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

    @JsonProperty("deviceId")
    private final String deviceId;

    /** host address of webrelay */
    @JsonProperty("host")
    private final String host;

    /** Port of webrelay (default 80) */
    @JsonProperty("port")
    private final int port;

    /** Port used to read temp sensor */
    @JsonProperty("serialPort")
    private final String serialPort;

    @JsonProperty("stubTemparature")
    private final boolean stubTemparature;

    @JsonProperty("stubRelay")
    private final boolean stubRelay;

    @JsonProperty("minTemp")
    private final double minTemp;

    @JsonProperty("maxTemp")
    private final double maxTemp;

    @JsonProperty("maxMargin")
    private final int maxMargin;

    @JsonProperty("market")
    private final MarketConfig market;

    @JsonCreator
    public FritzyConfig(@JsonProperty("deviceId") String deviceId,
            @JsonProperty("host") String host,
            @JsonProperty("port") int port,
            @JsonProperty("serialPort") String serialPort,
            @JsonProperty("stubTemparature") boolean stubTemparature,
            @JsonProperty("stubRelay") boolean stubRelay,
            @JsonProperty("minTemp") double minTemp,
            @JsonProperty("maxTemp") double maxTemp,
            @JsonProperty("maxMargin") int maxMargin,
            @JsonProperty("market") MarketConfig market) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.serialPort = serialPort;
        this.stubTemparature = stubTemparature;
        this.stubRelay = stubRelay;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.maxMargin = maxMargin;
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

