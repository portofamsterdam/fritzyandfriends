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
public class BattyConfig extends FritzyAppConfig {

    @JsonProperty("deviceId")
    private String deviceId;

    /** host address of battery */
    @JsonProperty("host")
    private String host;

    /** API username */
    @JsonProperty("username")
    private String username;

    /** API password */
    @JsonProperty("password")
    private String password;

    /** API password */
    @JsonProperty("truststore")
    private String truststore;

    /** API password */
    @JsonProperty("truststorepass")
    private String truststorepass;

    @JsonProperty("buyMargin")
    private int buyMargin;

    @JsonProperty("sellMargin")
    private int sellMargin;

    @JsonProperty("capactity")
    private double capacity;


    @JsonProperty("useStub")
    private boolean useStub;

    /**
     * Generate exxy config
     * 
     * @param args none
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {

        ObjectMapper mapper = JacksonFactory.defaultMapper();
        BattyConfig c = new BattyConfig();
        c.deviceId = "batty";
        c.setEnvironment("live");

        c.host = "192.168.8.241";
        c.username = "batty";
        c.password = "batty";
        c.truststore = "/opt/config/batty/truststore.jks";
        c.truststorepass = "12345678";
        c.buyMargin = 2;
        c.sellMargin = 2;
        c.capacity = 4.6d;
        c.useStub = false;

        MarketConfig market = new MarketConfig(false, "http://82.196.13.251/api", "batty@fritzy.nl", "batty");
        c.setMarket(market);

        ApiConfig apiConfig = new ApiConfig();
        ApiConfigRecord netty = new ApiConfigRecord(EApiNames.NETTY.getName(), "http://netty:8080/", 5000, 5000);
        ApiConfigRecord exxy = new ApiConfigRecord(EApiNames.EXXY.getName(), "http://exxy:8080/", 5000, 5000);
        apiConfig.setApis(Lists.newArrayList(netty, exxy));
        c.setApiConfig(apiConfig);

        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, c);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTruststore() {
        return truststore;
    }

    public void setTruststore(String truststore) {
        this.truststore = truststore;
    }

    public String getTruststorepass() {
        return truststorepass;
    }

    public void setTruststorepass(String truststorepass) {
        this.truststorepass = truststorepass;
    }

    public int getBuyMargin() {
        return buyMargin;
    }

    public void setBuyMargin(int buyMargin) {
        this.buyMargin = buyMargin;
    }

    public int getSellMargin() {
        return sellMargin;
    }

    public void setSellMargin(int sellMargin) {
        this.sellMargin = sellMargin;
    }

    public boolean isUseStub() {
        return useStub;
    }

    public void setUseStub(boolean useStub) {
        this.useStub = useStub;
    }

    public double getCapactity() {
        return capacity;
    }

    public void setCapactity(double capactity) {
        this.capacity = capactity;
    }
}

