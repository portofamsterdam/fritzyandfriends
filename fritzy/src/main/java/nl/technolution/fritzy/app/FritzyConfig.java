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

    @JsonProperty("market")
    private final MarketConfig market;

    @JsonCreator
    public FritzyConfig(@JsonProperty("deviceId") String deviceId,
            @JsonProperty("host") String host,
            @JsonProperty("port") int port,
            @JsonProperty("serialPort") String serialPort,
            @JsonProperty("market") MarketConfig market) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.serialPort = serialPort;
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

    public String getSerailPort() {
        return serialPort;
    }

    public MarketConfig getMarket() {
        return market;
    }
}

