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
package nl.technolution.batty.xstorage;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.technolution.batty.xstorage.types.MeterInfo;

/**
 * 
 */
public class MeterInfoTest {

    /**
     * 
     */
    @Test
    public void meterInfoTest() {
        // Example data from spec
        String data = "9,12,2,159,255,102,0,18,0,154,252,31,19,136,0,0,0,0,0,0";
        MeterInfo info = MeterInfo.fromData(data);
        assertEquals(231.6d, info.getVoltage(), 0.01);
        assertEquals(6.71d, info.getCurrent(), 0.001);

        assertEquals(-0.993, info.getPowerFactor(), 0.0001);
        assertEquals(50.00d, info.getFrequency(), 0.001);

        assertEquals(-1540, info.getActivePower());
        assertEquals(180, info.getReactivePower());
        assertEquals(1540, info.getApparentPower());
    }

}
