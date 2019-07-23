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
package nl.technolution.apis;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.IJsonnable;

/**
 * Record for Api
 */
public class ApiConfigRecord implements IJsonnable {
    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("connectTimeout")
    private int connectTimeout;
    @JsonProperty("readTimeout")
    private int readTimeout;

    public ApiConfigRecord() {
        //
    }

    /**
     * Constructor for {@link ApiConfigRecord} objects
     *
     * @param name
     * @param url
     * @param connectTimeout
     * @param readTimeout
     */
    public ApiConfigRecord(String name, String url, int connectTimeout, int readTimeout) {
        this.name = name;
        this.url = url;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

}