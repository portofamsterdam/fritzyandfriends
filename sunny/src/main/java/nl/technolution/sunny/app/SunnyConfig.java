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

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import nl.technolution.apis.ApiConfig;
import nl.technolution.apis.ApiConfigRecord;
import nl.technolution.apis.EApiNames;
import nl.technolution.dropwizard.FritzyAppConfig;
import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.dropwizard.webservice.JacksonFactory;

/**
 * Configuration for Fritzy
 */
public class SunnyConfig extends FritzyAppConfig {

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("market")
    private MarketConfig market;

    @JsonProperty("marketPriceStartOffset")
    private double marketPriceStartOffset;

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

    /**
     * Write a config
     * 
     * @param args
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
        // print config
        ObjectMapper mapper = JacksonFactory.defaultMapper();
        SunnyConfig c = new SunnyConfig();
        c.deviceId = "sunny";
        c.market = new MarketConfig("http://82.196.13.251/api", "sunny@fritzy.nl", "sunny");
        c.marketPriceStartOffset = 1;
        c.useStub = false;
        c.solarEdgeMonitoringBaseURL = "https://monitoringapi.solaredge.com/site/529405";
        c.solarEdgeMonitoringApikey = "JRK97634IPJD9ABBG4MFACJVZGLK4NUN";
        c.pvCastBaseURL = "https://api.pvcast.de/plants/908";
        c.pvCastApiKey = "cxDhZtryzwyGHG2yMzqy";
        c.solarEdgeModbusIpAddress = "192.168.8.240";
        c.solarEdgeModbusPort = 502;
        c.solarEdgeModbusDeviceId = 2;
        ApiConfig apiConfig = new ApiConfig();
        ApiConfigRecord netty = new ApiConfigRecord(EApiNames.NETTY.getName(), "http://localhost:8083/", 5000, 5000);
        ApiConfigRecord exxy = new ApiConfigRecord(EApiNames.EXXY.getName(), "http://localhost:8085/", 5000, 5000);
        apiConfig.setApis(Lists.newArrayList(netty, exxy));
        c.setApiConfig(apiConfig);
        c.setEnvironment("production");
        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, c);
    }

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

    public double getMarketPriceStartOffset() {
        return marketPriceStartOffset;
    }

    public void setMarketPriceStartOffset(double marketPriceStartOffset) {
        this.marketPriceStartOffset = marketPriceStartOffset;
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
