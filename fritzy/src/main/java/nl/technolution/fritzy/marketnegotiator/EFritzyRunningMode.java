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
package nl.technolution.fritzy.marketnegotiator;

import java.util.Arrays;

/**
 * 
 */
public enum EFritzyRunningMode {

    OFF(0),

    ON(1);

    private final int runningModeId;

    EFritzyRunningMode(int runningModeId) {
        this.runningModeId = runningModeId;
    }

    /**
     * Find based on runningmode Id
     * 
     * @param runningModeId to find
     * @return EFritzyInstruction
     */
    public static EFritzyRunningMode fromRunningModeId(int runningModeId) {
        return Arrays.asList(values())
                .stream()
                .filter(e -> e.runningModeId == runningModeId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Find based on boolean
     * 
     * @param isCooling
     * @return
     */
    public static EFritzyRunningMode fromIsCooling(boolean isCooling) {
        return (isCooling ? ON : OFF);
    }

    public int getRunningModeId() {
        return runningModeId;
    }
}
