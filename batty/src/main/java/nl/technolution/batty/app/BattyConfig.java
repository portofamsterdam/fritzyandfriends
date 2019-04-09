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
package nl.technolution.batty.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import nl.technolution.market.MarketConfig;

/**
 * Configuration for Fritzy
 */
public class BattyConfig extends Configuration {

    @JsonProperty("deviceId")
    private final String deviceId;

    /** host address of battery */
    @JsonProperty("host")
    private final String host;

    /** API username */
    @JsonProperty("username")
    private final String username;

    /** API password */
    @JsonProperty("username")
    private final String password;

    @JsonProperty("market")
    private MarketConfig market;

    @JsonCreator
    public BattyConfig(@JsonProperty("deviceId") String deviceId,
            @JsonProperty("host") String host,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("market") MarketConfig market) {
        this.deviceId = deviceId;
        this.host = host;
        this.username = username;
        this.password = password;
        this.market = market;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public MarketConfig getMarket() {
        return market;
    }
}

