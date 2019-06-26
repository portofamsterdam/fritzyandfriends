/*
 (C) COPYRIGHT 2016 TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.solaredge.sunspec;

/**
 * Modbus data types
 */
public enum EModbusDataType {
    FLOAT32(4, Float.class),
    UINT16(2, UnsignedShort.class),
    INT16(2, Short.class),
    UINT32(4, UnsignedInteger.class),
    INT32(4, Integer.class),
    UINT64(8, UnsignedLong.class),
    INT64(8, Long.class),
    STRING(1, String.class);

    private int size;
    private Class<?> type;

    EModbusDataType(int size, Class<?> type) {
        this.size = size;
        this.type = type;
    }

    public int getSize() {
        return this.size;
    }

    public Class<?> getType() {
        return this.type;
    }
}
