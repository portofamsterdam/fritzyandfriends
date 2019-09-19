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

import nl.technolution.IEnumBitset;

/**
 * 
 */
public enum EWarningType1 implements IEnumBitset {
    /** */
    RESERVED_0(0),
    /** */
    RESERVED_1(1),
    /** */
    ERRORCODE34(2),
    /** */
    RESERVED_3(3),
    /** */
    PVOVERPOWER(4),
    /** */
    RESERVED_5(5),
    /** */
    RESERVED_6(6),
    /** */
    RESERVED_7(7),
    /** General BMS fault detected */
    BMSFAULT(8);

    private final long bitMask;

    EWarningType1(int bitNr) {
        this.bitMask = 1L << bitNr;
    }

    @Override
    public long getMask() {
        return bitMask;
    }
}
