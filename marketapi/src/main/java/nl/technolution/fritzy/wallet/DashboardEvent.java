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
package nl.technolution.fritzy.wallet;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.IJsonnable;

/**
 * 
 */
public final class DashboardEvent implements IJsonnable {

    @JsonProperty("environment")
    private final String environment;

    @JsonProperty("actor")
    private final String actor;
    @JsonProperty("msg")
    private final String msg;

    @JsonProperty("tag")
    private final String tag;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss-Z")
    private Date timestamp;

    @JsonProperty("data")
    private final String data;

    public DashboardEvent(@JsonProperty("environment") String environment,
            @JsonProperty("actor") String actor,
            @JsonProperty("msg") String msg,
            @JsonProperty("tag") String tag,
            @JsonProperty("timestamp") Date timestamp,
            @JsonProperty("data") String data) {
        this.environment = environment;
        this.timestamp = timestamp;
        this.actor = actor;
        this.tag = tag;
        this.msg = msg;
        this.data = data;
    }
}
