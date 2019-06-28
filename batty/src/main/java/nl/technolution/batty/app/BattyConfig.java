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

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.dropwizard.FritzyAppConfig;

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

    @JsonProperty("useStub")
    private boolean useStub;

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
}

