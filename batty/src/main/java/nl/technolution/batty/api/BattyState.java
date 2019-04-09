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
package nl.technolution.batty.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 */
public class BattyState {

    @JsonProperty("isCooling")
    private final String batteryState;

    @JsonProperty("temparature")
    private final double chargeLevel;

    @JsonCreator
    public BattyState(@JsonProperty("batteryState") String batteryState,
            @JsonProperty("chargeLevel") double chargeLevel) {
        this.batteryState = batteryState;
        this.chargeLevel = chargeLevel;
    }

    public String getBatteryState() {
        return batteryState;
    }

    public double getChargeLevel() {
        return chargeLevel;
    }
}
