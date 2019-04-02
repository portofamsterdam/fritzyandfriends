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

public class MachineData {
    // 0 Time 19 -YYYY-MM-DD hh:mm:ss ASCII code
    private String time;
    // 1 Serial Number 16 - ASCII code
    private String serial;
    // 2 Temperature 4 0.1 degree C Dec(hex(3)hex(2)hex(1)hex(0))
    private double temperature;
    // 3 Vpv1 4 0.1V Dec(hex(3)hex(2)hex(1)hex(0))
    private double vpv1;
    // 4 Ipv1 4 0.1A Dec(hex(3)hex(2)hex(1)hex(0))
    private double ipv1;
    // 5 VBatSys 4 0.1V Dec(hex(3)hex(2)hex(1)hex(0))
    private double vBatSys;
    // 6 IBatSys 4 0.001A Dec(hex(3)hex(2)hex(1)hex(0))
    private double iBatSys;
    // 7 SOC 4 1% Dec(hex(3)hex(2)hex(1)hex(0))
    private int sOC;
    // 8 MTF 4 1 min Dec(hex(3)hex(2)hex(1)hex(0))
    private int mTF;
    // 9 MTE 4 1 min Dec(hex(3)hex(2)hex(1)hex(0))
    private int mTE;
    // 10 Iac 4 0.1A Dec(hex(3)hex(2)hex(1)hex(0))
    private double iac;
    // 11 Vac 4 0.1V Dec(hex(3)hex(2)hex(1)hex(0))
    private double vac;
    // 12 GVFaultValue 4 0.1V Dec(hex(3)hex(2)hex(1)hex(0))
    private double gVFaultValue;
    // 13 TmpFaultValue 4 0.1 Degree C Dec(hex(3)hex(2)hex(1)hex(0))
    private double tmpFaultValue;
    // 14 PV1FaultValue 4 0.1V Dec(hex(3)hex(2)hex(1)hex(0))
    private double pV1FaultValue;
    // 15 Pac 4 W Dec(hex(3)hex(2)hex(1)hex(0))
    private double pac;
    // 16 E_Today 4 0.01KW-Hr Dec(hex(3)hex(2)hex(1)hex(0))
    private double eToday;
    // 17 Fac 4 0.01Hz Dec(hex(3)hex(2)hex(1)hex(0))
    private double fac;
    // 18 GFFaultValue 4 0.01Hz (Grid frequency fault value ) Dec(hex(3)hex(2)hex(1)hex(0))
    private double gFFaultValue;
    // 19 GFCIFaultValue 4 0.001A (GFCI curren0t fault value) Dec(hex(3)hex(2)hex(1)hex(0))
    private double gFCIFaultValue;
    // 20 E_Total 4 0.1KW-Hr Dec(hex(3)hex(2)hex(1)hex(0))
    private double eTotal;
    // 21 H_Total 4 Hr Dec(hex(3)hex(2)hex(1)hex(0))
    private int hTotal;
    // 22 Warning message1 4 Table 3.2.2.2 hex(3)hex(2)hex(1)hex(0))
    private EWaringType1 warning1;
    // 23 Warning message2 4 Table 3.2.2.3 hex(3)hex(2)hex(1)hex(0))
    private EWaringType2 warning2;
    // 24 BMS state 4 BMS operation mode Dec(hex(3)hex(2)hex(1)hex(0))
    private int bMS;
    // 25 Mode 4 Table 3.2.2.4 hex(3)hex(2)hex(1)hex(0) byte1 : hex(3)hex(2) byte0 : hex(1)hex(0)
    private EMode mode;
    // 26 Pload Power to back-up load (R Phase) 4 W Dec(hex(3)hex(2)hex(1)hex(0))
    private double pload;
    // 27 E_Draw (Energy from grid(R Phase)) 4 0.1KW-Hr Dec(hex(3)hex(2)hex(1)hex(0))
    private double eDraw;

    private MachineData() {
        // 
    }

    /**
     * @param data
     * @return
     */
    public static MachineData fromData(String[] data) {
        MachineData machineData = new MachineData();
        return machineData;
    }

}
