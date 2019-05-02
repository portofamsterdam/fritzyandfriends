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

public class UnsignedLong extends Number {
	private static final BigInteger MASK = new BigInteger("FFFFFFFFFFFFFFFF", 16);
	
	private final BigInteger value;
	
    public UnsignedLong(BigInteger value) {
        this.value = value;
    }
	
	public BigInteger getValue() {
		return value.and(MASK);
	}
	
	public long getSignedValue() {
		return value.and(MASK).longValue();
	}
	
	@Override
	public String toString() {
		return value.and(MASK).toString();
	}
	
    public String toString(int radix) {
        return value.and(MASK).toString(radix);
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
