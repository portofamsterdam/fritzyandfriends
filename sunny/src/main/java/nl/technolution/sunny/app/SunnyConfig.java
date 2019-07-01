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

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;

/**
 * Configuration for Fritzy
 */
public class SunnyConfig extends FritzyAppConfig {

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("market")
    private MarketConfig market;

    @JsonProperty("useStub")
    private boolean useStub;

    @JsonProperty("solarEdgeMonitoringBaseURL")
    private String solarEdgeMonitoringBaseURL;

    @JsonProperty("solarEdgeMonitoringApikey")
    private String solarEdgeMonitoringApikey;

    @JsonProperty("pvCastBaseURL")
    private String pvCastBaseURL;

    @JsonProperty("pvCastApiKey")
    private Object pvCastApiKey;

    @JsonProperty("solarEdgeModbusIpAddress")
    private String solarEdgeModbusIpAddress;

    @JsonProperty("solarEdgeModbusPort")
    private int solarEdgeModbusPort;

    @JsonProperty("solarEdgeModbusDeviceId")
    private int solarEdgeModbusDeviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public MarketConfig getMarket() {
        return market;
    }

    public void setMarket(MarketConfig market) {
        this.market = market;
    }

    public boolean isUseStub() {
        return useStub;
    }

    public void setUseStub(boolean useStub) {
        this.useStub = useStub;
    }

    public String getSolarEdgeMonitoringBaseURL() {
        return solarEdgeMonitoringBaseURL;
    }

    public void setSolarEdgeMonitoringBaseURL(String solarEdgeMonitoringBaseURL) {
        this.solarEdgeMonitoringBaseURL = solarEdgeMonitoringBaseURL;
    }

    public String getSolarEdgeMonitoringApikey() {
        return solarEdgeMonitoringApikey;
    }

    public void setSolarEdgeMonitoringApikey(String solarEdgeMonitoringApikey) {
        this.solarEdgeMonitoringApikey = solarEdgeMonitoringApikey;
    }

    public String getPvCastBaseURL() {
        return pvCastBaseURL;
    }

    public void setPvCastBaseURL(String pvCastBaseURL) {
        this.pvCastBaseURL = pvCastBaseURL;
    }

    public Object getPvCastApiKey() {
        return pvCastApiKey;
    }

    public void setPvCastApiKey(Object pvCastApiKey) {
        this.pvCastApiKey = pvCastApiKey;
    }

    public String getSolarEdgeModbusIpAddress() {
        return solarEdgeModbusIpAddress;
    }

    public void setSolarEdgeModbusIpAddress(String solarEdgeModbusIpAddress) {
        this.solarEdgeModbusIpAddress = solarEdgeModbusIpAddress;
    }

    public int getSolarEdgeModbusPort() {
        return solarEdgeModbusPort;
    }

    public void setSolarEdgeModbusPort(int solarEdgeModbusPort) {
        this.solarEdgeModbusPort = solarEdgeModbusPort;
    }

    public int getSolarEdgeModbusDeviceId() {
        return solarEdgeModbusDeviceId;
    }

    public void setSolarEdgeModbusDeviceId(int solarEdgeModbusDeviceId) {
        this.solarEdgeModbusDeviceId = solarEdgeModbusDeviceId;
    }
}
