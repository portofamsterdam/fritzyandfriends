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
    private final String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("connectTimeout")
    private int connectTimeout;
    @JsonProperty("readTimeout")
    private int readTimeout;

    /**
    *
    * @param url to connect to
    * @param connectTimeout timeout after connect ms
    * @param readTimeout timeout after read ms
    */
    public ApiConfigRecord(@JsonProperty("name") String name,
            @JsonProperty("url") String url,
           @JsonProperty("connectTimeout") int connectTimeout,
           @JsonProperty("readTimeout") int readTimeout) {
        this.name = name;
        this.url = url;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
   }

    public String getName() {
        return name;
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