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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

import nl.technolution.IEnumBitset;

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
    private Set<EWarningType1> warning1;
    // 23 Warning message2 4 Table 3.2.2.3 hex(3)hex(2)hex(1)hex(0))
    private Set<EWarningType2> warning2;
    // 24 BMS state 4 BMS operation mode Dec(hex(3)hex(2)hex(1)hex(0))
    private int bMS;
    // 25 Mode 4 Table 3.2.2.4 hex(3)hex(2)hex(1)hex(0) byte1 : hex(3)hex(2) byte0 : hex(1)hex(0)
    private Mode mode;
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
    public static MachineData fromData(List<Integer[]> data) {
        MachineData machineData = new MachineData();
        Preconditions.checkArgument(data.size() == 28, "Unexpected number of elements");
        machineData.time = parseString(data.get(0));
        machineData.serial = parseString(data.get(1));
        machineData.temperature = parseDouble(data.get(2), 0.1d);
        machineData.vpv1 = parseDouble(data.get(3), 0.1d);
        machineData.ipv1 = parseDouble(data.get(4), 0.1d);
        machineData.vBatSys = parseDouble(data.get(5), 0.1d);
        machineData.iBatSys = parseDouble(data.get(6), 0.001d);
        machineData.sOC = parseInt(data.get(7));
        machineData.mTF = parseInt(data.get(8));
        machineData.mTE = parseInt(data.get(9));
        machineData.iac = parseInt(data.get(10));
        machineData.vac = parseInt(data.get(11));
        machineData.gVFaultValue = parseDouble(data.get(12), 0.1d);
        machineData.tmpFaultValue = parseDouble(data.get(13), 0.1d);
        machineData.pV1FaultValue = parseDouble(data.get(14), 0.1d);
        machineData.pac = parseInt(data.get(15));
        machineData.eToday = parseDouble(data.get(16), 0.01d);
        machineData.fac = parseDouble(data.get(17), 0.01d);
        machineData.gFFaultValue = parseDouble(data.get(18), 0.01d);
        machineData.gFCIFaultValue = parseDouble(data.get(19), 0.01d);
        machineData.eTotal = parseDouble(data.get(20), 0.1d);
        machineData.hTotal = parseInt(data.get(21));
        machineData.warning1 = IEnumBitset.getEnumSet(parseInt(data.get(22)), EWarningType1.class);
        machineData.warning2 = IEnumBitset.getEnumSet(parseInt(data.get(23)), EWarningType2.class);
        machineData.bMS = parseInt(data.get(24));
        machineData.mode = Mode.getModeByInt(parseInt(data.get(25)));
        machineData.pload = parseInt(data.get(26));
        machineData.eDraw = parseDouble(data.get(27), 0.1d);

        return machineData;
    }

    private static int parseInt(Integer[] integers) {
        int result = 0;
        result += integers[0] << 0;
        result += integers[1] << 8;
        result += integers[2] << 16;
        result += integers[3] << 24;
        return result;
    }

    private static double parseDouble(Integer[] integers, double scale) {
        int result = 0;
        result += integers[0] << 0;
        result += integers[1] << 8;
        result += integers[2] << 16;
        result += integers[3] << 24;
        return result * scale;
    }

    private static String parseString(Integer[] integers) {
        return Arrays.asList(integers)
                .stream()
                .filter(i -> i != 0)
                .map(i -> (char)i.intValue())
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String getTime() {
        return time;
    }

    public String getSerial() {
        return serial;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getVpv1() {
        return vpv1;
    }

    public double getIpv1() {
        return ipv1;
    }

    public double getvBatSys() {
        return vBatSys;
    }

    public double getiBatSys() {
        return iBatSys;
    }

    public int getsOC() {
        return sOC;
    }

    public int getmTF() {
        return mTF;
    }

    public int getmTE() {
        return mTE;
    }

    public double getIac() {
        return iac;
    }

    public double getVac() {
        return vac;
    }

    public double getgVFaultValue() {
        return gVFaultValue;
    }

    public double getTmpFaultValue() {
        return tmpFaultValue;
    }

    public double getpV1FaultValue() {
        return pV1FaultValue;
    }

    public double getPac() {
        return pac;
    }

    public double geteToday() {
        return eToday;
    }

    public double getFac() {
        return fac;
    }

    public double getgFFaultValue() {
        return gFFaultValue;
    }

    public double getgFCIFaultValue() {
        return gFCIFaultValue;
    }

    public double geteTotal() {
        return eTotal;
    }

    public int gethTotal() {
        return hTotal;
    }

    public Set<EWarningType1> getWarning1() {
        return warning1;
    }

    public Set<EWarningType2> getWarning2() {
        return warning2;
    }

    public int getbMS() {
        return bMS;
    }

    public Mode getMode() {
        return mode;
    }

    public double getPload() {
        return pload;
    }

    public double geteDraw() {
        return eDraw;
    }

    @Override
    public String toString() {
        return "MachineData [time=" + time + ", serial=" + serial + ", temperature=" + temperature + ", vpv1=" + vpv1 +
                ", ipv1=" + ipv1 + ", vBatSys=" + vBatSys + ", iBatSys=" + iBatSys + ", sOC=" + sOC + ", mTF=" + mTF +
                ", mTE=" + mTE + ", iac=" + iac + ", vac=" + vac + ", gVFaultValue=" + gVFaultValue +
                ", tmpFaultValue=" + tmpFaultValue + ", pV1FaultValue=" + pV1FaultValue + ", pac=" + pac + ", eToday=" +
                eToday + ", fac=" + fac + ", gFFaultValue=" + gFFaultValue + ", gFCIFaultValue=" + gFCIFaultValue +
                ", eTotal=" + eTotal + ", hTotal=" + hTotal + ", warning1=" + warning1 + ", warning2=" + warning2 +
                ", bMS=" + bMS + ", mode=" + mode + ", pload=" + pload + ", eDraw=" + eDraw + "]";
    }
}
