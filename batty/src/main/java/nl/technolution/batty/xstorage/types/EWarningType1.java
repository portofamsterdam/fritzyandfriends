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
    Reserved_0(0),
    /** */
    Reserved_1(1),
    /** */
    ErrorCode34(2),
    /** */
    Reserved_3(3),
    /** */
    PvOverPower(4),
    /** */
    Reserved_5(5),
    /** */
    Reserved_6(6),
    /** */
    Reserved_7(7),
    /** General BMS fault detected */
    BmsFault(8);

    private final long bitMask;

    EWarningType1(int bitNr) {
        this.bitMask = 1L << bitNr;
    }

    @Override
    public long getMask() {
        return bitMask;
    }
}
