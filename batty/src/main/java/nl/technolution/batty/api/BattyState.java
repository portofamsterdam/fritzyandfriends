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

import nl.technolution.IJsonnable;

/**
 * 
 */
public class BattyState implements IJsonnable {

    @JsonProperty("batteryState")
    private final String batteryState;

    @JsonProperty("chargeLevel")
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
