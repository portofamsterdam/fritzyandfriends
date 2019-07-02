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
package nl.technolution.apis.netty;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.technolution.IJsonnable;

/**
 * 
 */
public class DeviceCapacity implements IJsonnable {

    @JsonProperty("gridConnectionLimit")
    private double gridConnectionLimit;

    @JsonProperty("groupConnectionLimit")
    private double groupConnectionLimit;

    /**
     * Constructor for {@link DeviceCapacity} objects
     *
     */
    public DeviceCapacity() {

    }

    /**
     * Constructor for {@link DeviceCapacity} objects
     *
     * @param gridConnectionLimit
     * @param groupConnectionLimit
     */
    public DeviceCapacity(double gridConnectionLimit, double groupConnectionLimit) {
        this.gridConnectionLimit = gridConnectionLimit;
        this.groupConnectionLimit = groupConnectionLimit;
    }

    public double getGridConnectionLimit() {
        return gridConnectionLimit;
    }

    public void setGridConnectionLimit(double gridConnectionLimit) {
        this.gridConnectionLimit = gridConnectionLimit;
    }

    public double getGroupConnectionLimit() {
        return groupConnectionLimit;
    }

    public void setGroupConnectionLimit(double groupConnectionLimit) {
        this.groupConnectionLimit = groupConnectionLimit;
    }

}
