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
package nl.technolution.batty.xstorage.types;

import java.util.Arrays;

/**
 * All API call return JSON with one data element.
 */
public class MachineInfoJson {

    private String[] data;

    /**
     * @return copy of set
     */
    public String[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * @param data store copy of
     */
    public void setData(String[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }
}
