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

    /** EFI id of the device */
    @JsonProperty("deviceId")
    private String deviceId;

    /** offset in euro cent used at the start of the negation. First bid will be marketprice + offset. */
    @JsonProperty("marketPriceStartOffset")
    private double marketPriceStartOffset;

    /** use stub instead of inverter */
    @JsonProperty("useSolarEdgeStub")
    private boolean useSolarEdgeStub;

    /** base URL to SolarEdge monitoring portal. Used to retrieve hourly values which are needed by pvCast */
    @JsonProperty("solarEdgeMonitoringBaseURL")
    private String solarEdgeMonitoringBaseURL;

    /** API key for SolarEdge monitoring portal. */
    @JsonProperty("solarEdgeMonitoringApikey")
    private String solarEdgeMonitoringApikey;

    /** base URL to pvCast. pvCast supplies the forecasts so Sunny 'knows' how much energy it has to offer */
    @JsonProperty("pvCastBaseURL")
    private String pvCastBaseURL;

    /** API key for pvCast. */
    @JsonProperty("pvCastApiKey")
    private Object pvCastApiKey;

    /** IP address of the SolarEdge inverter */
    @JsonProperty("solarEdgeModbusIpAddress")
    private String solarEdgeModbusIpAddress;

    /** modbus port of the SolarEdge inverter */
    @JsonProperty("solarEdgeModbusPort")
    private int solarEdgeModbusPort;

    /** modbus device id of the SolarEdge inverter */
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
        MarketConfig market = new MarketConfig(false, "http://82.196.13.251/api", "sunny@fritzy.nl", "sunny");
        c.setMarket(market);
        c.marketPriceStartOffset = 1;
        c.useSolarEdgeStub = false;
        c.solarEdgeMonitoringBaseURL = "https://monitoringapi.solaredge.com/site/529405";
        c.solarEdgeMonitoringApikey = "JRK97634IPJD9ABBG4MFACJVZGLK4NUN";
        c.pvCastBaseURL = "https://api.pvcast.de/plants/908";
        c.pvCastApiKey = "cxDhZtryzwyGHG2yMzqy";
        c.solarEdgeModbusIpAddress = "192.168.8.240";
        c.solarEdgeModbusPort = 502;
        c.solarEdgeModbusDeviceId = 2;
        ApiConfig apiConfig = new ApiConfig();
        ApiConfigRecord netty = new ApiConfigRecord(EApiNames.NETTY.getName(), "http://netty:8080/", 5000, 5000);
        ApiConfigRecord exxy = new ApiConfigRecord(EApiNames.EXXY.getName(), "http://exxy:8080/", 5000, 5000);
        apiConfig.setApis(Lists.newArrayList(netty, exxy));
        c.setApiConfig(apiConfig);
        c.setEnvironment("live");
        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, c);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getMarketPriceStartOffset() {
        return marketPriceStartOffset;
    }

    public void setMarketPriceStartOffset(double marketPriceStartOffset) {
        this.marketPriceStartOffset = marketPriceStartOffset;
    }

    public boolean isUseSolarEdgeStub() {
        return useSolarEdgeStub;
    }

    public void setUseSolarEdgeStub(boolean useSolarEdgeStub) {
        this.useSolarEdgeStub = useSolarEdgeStub;
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
