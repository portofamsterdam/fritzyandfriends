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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import nl.technolution.apis.ApiConfig;
import nl.technolution.apis.ApiConfigRecord;
import nl.technolution.apis.EApiNames;
import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.webservice.JacksonFactory;

/**
 * Configuration for Netty
 */
public class NettyConfig extends FritzyAppConfig {

    @JsonProperty("defaultGridConnectionLimit")
    private String deviceId;

    @JsonProperty("defaultGridConnectionLimit")
    private double defaultGridConnectionLimit;

    @JsonProperty("groupConnectionLimit")
    private double groupConnectionLimit;

    @JsonProperty("deviceLimits")
    private Map<String, Double> deviceLimits;

    @JsonProperty("localusers")
    private Set<String> localusers;

    @JsonProperty("localreward")
    private double localReward;

    /**
     * Generate config
     * 
     * @param args
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {

        ObjectMapper mapper = JacksonFactory.defaultMapper();
        NettyConfig c = new NettyConfig();
        c.deviceId = "netty";
        c.setEnvironment("production");

        c.defaultGridConnectionLimit = 16;
        c.groupConnectionLimit = 32;
        c.deviceLimits = new HashMap<>();
        c.deviceLimits.put("fritzy", 8d);
        c.deviceLimits.put("sunny", 8d);
        c.deviceLimits.put("batty", 8d);

        c.localusers = Sets.newHashSet("fritzy", "sunny", "batty");
        c.localReward = 2d;

        MarketConfig market = new MarketConfig(false, "http://82.196.13.251/api", "netty@fritzy.nl", "netty");
        c.setMarket(market);

        ApiConfig apiConfig = new ApiConfig();
        ApiConfigRecord exxy = new ApiConfigRecord(EApiNames.EXXY.getName(), "http://exxy:8080/", 5000, 5000);
        apiConfig.setApis(Lists.newArrayList(exxy));
        c.setApiConfig(apiConfig);

        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, c);
    }

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

    public Set<String> getLocalusers() {
        return localusers;
    }

    public void setLocalusers(Set<String> localusers) {
        this.localusers = localusers;
    }

    public double getLocalReward() {
        return localReward;
    }

    public void setLocalReward(double localReward) {
        this.localReward = localReward;
    }

    public void setGroupConnectionLimit(double groupConnectionLimit) {
        this.groupConnectionLimit = groupConnectionLimit;
    }

}
