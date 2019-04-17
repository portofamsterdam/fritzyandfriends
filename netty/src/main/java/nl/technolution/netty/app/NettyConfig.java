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
package nl.technolution.netty.app;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * Configuration for Netty
 */
public class NettyConfig extends Configuration {

    @JsonProperty("defaultGridConnectionLimit")
    private final double defaultGridConnectionLimit;

    @JsonProperty("deviceLimits")
    private final Map<String, Double> deviceLimits;

    /**
     * Constructor for {@link NettyConfig} objects
     *
     * @param defaultLimit
     * @param limits
     */
    public NettyConfig(@JsonProperty("defaultGridConnectionLimit") double defaultGridConnectionLimit,
            @JsonProperty("limit") Map<String, Double> deviceLimits) {
        this.defaultGridConnectionLimit = defaultGridConnectionLimit;
        this.deviceLimits = deviceLimits;
    }

    public double getDefaultGridConnectionLimit() {
        return defaultGridConnectionLimit;
    }

    public Map<String, Double> getDeviceLimits() {
        return deviceLimits;
    }
}
