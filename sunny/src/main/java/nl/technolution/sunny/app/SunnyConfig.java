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
package nl.technolution.sunny.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.dropwizard.FritzyAppConfig;

/**
 * Configuration for Fritzy
 */
public class SunnyConfig extends FritzyAppConfig {

    @JsonProperty("deviceId")
    private final String deviceId;

    /** host address of battery */
    @JsonProperty("host")
    private final String host;

    /** Maximum margin used by Fritzy */
    @JsonProperty("maxMargin")
    private final int maxMargin;


    @JsonProperty("solarEdgeMonitoringBaseURL")
    private String solarEdgeMonitoringBaseURL;

    @JsonProperty("solarEdgeMonitoringApikey")
    private String solarEdgeMonitoringApikey;

    @JsonProperty("pvCastBaseURL")
    private String pvCastBaseURL;

    @JsonProperty("pvCastApiKey")
    private Object pvCastApiKey;

    @JsonCreator
    public SunnyConfig(@JsonProperty("deviceId") String deviceId, @JsonProperty("host") String host,
            @JsonProperty("maxMargin") int maxMargin,
            @JsonProperty("solarEdgeMonitoringBaseURL") String solarEdgeMonitoringBaseURL,
            @JsonProperty("solarEdgeMonitoringApikey") String solarEdgeMonitoringApikey,
            @JsonProperty("pvCastBaseURL") String pvCastBaseURL, @JsonProperty("pvCastApiKey") String pvCastApiKey) {
        this.deviceId = deviceId;
        this.host = host;
        this.maxMargin = maxMargin;
        this.solarEdgeMonitoringBaseURL = solarEdgeMonitoringBaseURL;
        this.solarEdgeMonitoringApikey = solarEdgeMonitoringApikey;
        this.pvCastBaseURL = pvCastBaseURL;
        this.pvCastApiKey = pvCastApiKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getHost() {
        return host;
    }

    public String getSolarEdgeMonitoringBaseURL() {
        return solarEdgeMonitoringBaseURL;
    }

    public String getSolarEdgeMonitoringApikey() {
        return solarEdgeMonitoringApikey;
    }

    public String getPvCastBaseURL() {
        return pvCastBaseURL;
    }

    public Object getPvCastApiKey() {
        return pvCastApiKey;
    }
}
