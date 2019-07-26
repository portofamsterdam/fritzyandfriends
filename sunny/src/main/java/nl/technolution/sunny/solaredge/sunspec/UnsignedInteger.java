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
package nl.technolution.sunny.solaredge.sunspec;

import java.math.BigInteger;

/**
 * 
 */
public class UnsignedInteger extends Number {

    private static final BigInteger MASK = new BigInteger("FFFFFFFF", 16);

    private final BigInteger value;

    public UnsignedInteger(int value) {
        this.value = BigInteger.valueOf(value);
    }

    public long getValue() {
        return value.and(MASK).longValue();
    }

    public int getSignedValue() {
        return value.and(MASK).intValue();
    }

    @Override
    public String toString() {
        return value.and(MASK).toString();
    }

    @Override
    public int intValue() {
        return value.and(MASK).intValue();
    }

    @Override
    public long longValue() {
        return value.and(MASK).longValue();
    }

    @Override
    public float floatValue() {
        return value.and(MASK).floatValue();
    }

    @Override
    public double doubleValue() {
        return value.and(MASK).doubleValue();
    }
}
