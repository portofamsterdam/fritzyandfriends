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

/**
 * 
 */
public final class Mode {

    private final EModeByte0 mode0;
    private final EModeByte1 mode1;

    private Mode(EModeByte0 mode0, EModeByte1 mode1) {
        this.mode0 = mode0;
        this.mode1 = mode1;
    }

    /**
     * Parse Mode for int value
     * 
     * @param mode to parse
     * @return mode with both mode states
     */
    public static Mode getModeByInt(int mode) {
        int mode1index = mode >> 16;
        int mode0index = mode & 0xFFFF;
        return new Mode(EModeByte0.fromIndex(mode0index), EModeByte1.fromIndex(mode1index));
    }

    public EModeByte0 getMode0() {
        return mode0;
    }

    public EModeByte1 getMode1() {
        return mode1;
    }

    @Override
    public int hashCode() {
        // mode0 and mode1 can only be build with 2 bytes conform spec. This yields unique int
        int hash = mode1.getIndex() << 16;
        hash += mode0.getIndex();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;       
        }
        Mode other = (Mode)obj;
        if (mode0 != other.mode0 || mode1 != other.mode1) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mode [mode0=" + mode0 + ", mode1=" + mode1 + "]";
    }
}
