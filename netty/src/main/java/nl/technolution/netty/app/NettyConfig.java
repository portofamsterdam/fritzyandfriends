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

import nl.technolution.dropwizard.FritzyAppConfig;

/**
 * Configuration for Netty
 */
public class NettyConfig extends FritzyAppConfig {

    @JsonProperty("defaultGridConnectionLimit")
    private double defaultGridConnectionLimit;

    @JsonProperty("groupConnectionLimit")
    private double groupConnectionLimit;

    @JsonProperty("deviceLimits")
    private Map<String, Double> deviceLimits;

    /**
     * Constructor for {@link NettyConfig} objects
     *
     * @param defaultGridConnectionLimit
     * @param groupConnectionLimit group connection limit
     * @param deviceLimits
     */
    public NettyConfig(double defaultGridConnectionLimit, double groupConnectionLimit,
            Map<String, Double> deviceLimits) {
        this.defaultGridConnectionLimit = defaultGridConnectionLimit;
        this.groupConnectionLimit = groupConnectionLimit;
        this.deviceLimits = deviceLimits;
    }

    /**
     * Constructor for {@link NettyConfig} objects
     */
    public NettyConfig() {
        //
    }

    public double getGroupConnectionLimit() {
        return groupConnectionLimit;
    }

    public double getDefaultGridConnectionLimit() {
        return defaultGridConnectionLimit;
    }

    public void setDefaultGridConnectionLimit(double defaultGridConnectionLimit) {
        this.defaultGridConnectionLimit = defaultGridConnectionLimit;
    }

    public Map<String, Double> getDeviceLimits() {
        return deviceLimits;
    }

    public void setDeviceLimits(Map<String, Double> deviceLimits) {
        this.deviceLimits = deviceLimits;
    }

}
