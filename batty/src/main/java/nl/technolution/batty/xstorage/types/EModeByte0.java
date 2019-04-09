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
public enum EModeByte0 {
    // System is ready to turn on and start to output power. There isn’t any fault in this state and no output power
    // transmitted to grid or load.
    WAIT(0),
    // In the normal state, the system could be grid connected or load protected depends on the condition of grid. The
    // power flow will follow the specific command and system condition for peak shaving, frequency regulation or
    // protecting load.
    NORMAL(1),
    // Warning status occurs: execute the protect steps, insulate the grid voltage to system, detect the grid voltage
    // and fault and judge whether the fault has been removed. The state can be resumed automatically after the fault
    // has been removed. The condition of entering warning mode:
    // 1. Temperature too high
    // 2. Ground current too high
    // 3. PV voltage too high
    // 4. Grid frequency out of range
    // 5. Grid voltage out of range
    // 6. Isolation resistance too low
    WARNING(2),

    // System Fault. The protect steps will be executed and auto restart disable
    // The condition of entering Permanent Fault mode:
    // 1. Grid current DC offset
    // 2. Grid current too high
    // 3. Eeprom cannot be read or write in
    // 4. Communication between CPU is fail
    // 5. Bus Voltage too low or high
    // 6. Compare measured value from two CPU
    // 7. Main bridge relay fail
    PERMANENT_FAULT(3);

    private final int index;

    EModeByte0(int index) {
        this.index = index;
    }

    /**
     * Find mode for index
     * 
     * @param index to find
     * @return mode (byte 0)
     */
    public static EModeByte0 fromIndex(int index) {
        for (EModeByte0 mode0 : values()) {
            if (mode0.index == index) {
                return mode0;
            }
        }
        throw new IllegalArgumentException("unspecified index " + index);
    }

    public int getIndex() {
        return index;
    }
}
