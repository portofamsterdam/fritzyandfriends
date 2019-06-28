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
package nl.technolution.fritzy.app;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.dropwizard.FritzyAppConfig;

/**
 * Configuration for Fritzy
 */
public class FritzyConfig extends FritzyAppConfig {

    @JsonProperty("deviceId")
    private String deviceId;

    /** host address of webrelay */
    @JsonProperty("host")
    private String host;

    /** Port of webrelay (default 80) */
    @JsonProperty("port")
    private int port;

    /** Port used to read temp sensor */
    @JsonProperty("serialPort")
    private String serialPort;

    @JsonProperty("stubTemparature")
    private boolean stubTemparature;

    @JsonProperty("stubRelay")
    private boolean stubRelay;

    @JsonProperty("minTemp")
    private double minTemp;

    @JsonProperty("maxTemp")
    private double maxTemp;

    @JsonProperty("maxMargin")
    private int maxMargin;

    public FritzyConfig() {
        // Empty constructor
    }

    public FritzyConfig(String deviceId, String host, int port, String serialPort, boolean stubTemparature,
            boolean stubRelay, double minTemp, double maxTemp, int maxMargin) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.serialPort = serialPort;
        this.stubTemparature = stubTemparature;
        this.stubRelay = stubRelay;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.maxMargin = maxMargin;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public boolean isStubTemparature() {
        return stubTemparature;
    }

    public void setStubTemparature(boolean stubTemparature) {
        this.stubTemparature = stubTemparature;
    }

    public boolean isStubRelay() {
        return stubRelay;
    }

    public void setStubRelay(boolean stubRelay) {
        this.stubRelay = stubRelay;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMaxMargin() {
        return maxMargin;
    }

    public void setMaxMargin(int maxMargin) {
        this.maxMargin = maxMargin;
    }

}
