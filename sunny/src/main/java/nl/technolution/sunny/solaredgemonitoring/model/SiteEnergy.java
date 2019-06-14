/*
 (C) COPYRIGHT 2019 TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.solaredgemonitoring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * SolarEdge Monitoring portal site energy object
 */
public class SiteEnergy {

    @JsonProperty("energy")
    private Energy energy;

    @JsonProperty("energy")
    public Energy getEnergy() {
        return energy;
    }

    @JsonProperty("energy")
    public void setEnergy(Energy energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("energy", energy).toString();
    }
}
