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
package nl.technolution.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.IJsonnable;

/**
 * 
 */
public final class DashboardEvent implements IJsonnable {

    @JsonProperty("environment")
    private final String environment;

    @JsonProperty("timestamp")
    private final long timestamp;

    @JsonProperty("actor")
    private final String actor;

    @JsonProperty("tag")
    private final String tag;

    @JsonProperty("event")
    private final String event;

    @JsonProperty("data")
    private final String data;

    public DashboardEvent(@JsonProperty("environment") String environment, @JsonProperty("timestamp") long timestamp,
            @JsonProperty("actor") String actor, @JsonProperty("tag") String tag, @JsonProperty("event") String event,
            @JsonProperty("data") String data) {
        this.environment = environment;
        this.timestamp = timestamp;
        this.actor = actor;
        this.tag = tag;
        this.event = event;
        this.data = data;
    }

    @Override
    public String toString() {
        return "DashboardEvent [environment=" + environment + ", timestamp=" + timestamp + ", actor=" + actor +
                ", tag=" + tag + ", event=" + event + ", data=" + data + "]";
    }
}
