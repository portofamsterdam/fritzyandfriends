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
package nl.technolution.fritzy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * Configuration for Fritzy
 */
public class FritzyConfig extends Configuration {

    @JsonProperty("deviceId")
    private final String deviceId;

    /** host address of webrelay */
    @JsonProperty("host")
    private final String host;

    /** Port of webrelay (default 80) */
    @JsonProperty("port")
    private final int port;

    /** Port used to read temp sensor */
    @JsonProperty("serialPort")
    private final String serialPort;

    @JsonCreator
    public FritzyConfig(@JsonProperty("deviceId") String deviceId, @JsonProperty("host") String host,
            @JsonProperty("port") int port, @JsonProperty("serialPort") String serialPort) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.serialPort = serialPort;
    }

    public String getDevicveId() {
        return deviceId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSerailPort() {
        return serialPort;
    }
}

