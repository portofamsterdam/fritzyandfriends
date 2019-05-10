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
package nl.technolution.fritzy.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.IJsonnable;

/**
 * 
 */
public class FritzyState implements IJsonnable {

    @JsonProperty("isCooling")
    private final boolean isCooling;

    @JsonProperty("temparature")
    private final double temparature;

    @JsonCreator
    public FritzyState(@JsonProperty("isCooling") boolean isCooling, @JsonProperty("temparature") double temparature) {
        this.isCooling = isCooling;
        this.temparature = temparature;
    }

    public boolean isCooling() {
        return isCooling;
    }

    public double getTemparature() {
        return temparature;
    }
}
