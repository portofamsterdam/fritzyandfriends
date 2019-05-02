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
public final class BmsData {

    // 0~1 1.5mV Highest cell voltage 2 Bytes Dec(Hex(0)hex(1))
    private double highestCellVoltage;
    // 2~3 1.5mV Lowest cell voltage 2 Bytes Dec(Hex(2)hex(3))
    private double lowestCellVoltage;
    // 4~5 0.1 C System maximum temperature 2 Bytes Dec(Hex(4)hex(5))
    private double systemMaxTemparature;
    // 6~7 0.1 C System average temperature 2 Bytes Dec(Hex(6)hex(7))
    private double systemAvgTemparature;
    // 8~9 0.1 C System minimum temperature 2 Bytes Dec(Hex(8)hex(9))
    private double systemMinTemparature;

    private BmsData() {
        // 
    }

    /**
     * @param highestCellVoltage
     * @param lowestCellVoltage
     * @param systemMaxTemparature
     * @param systemAvgTemparature
     * @param systemMinTemparature
     * @return
     */
    public static BmsData build(double highestCellVoltage, double lowestCellVoltage, double systemMaxTemparature,
            double systemAvgTemparature, double systemMinTemparature) {
        BmsData machineData = new BmsData();
        machineData.highestCellVoltage = highestCellVoltage;
        machineData.lowestCellVoltage = lowestCellVoltage;
        machineData.systemMaxTemparature = systemMaxTemparature;
        machineData.systemAvgTemparature = systemAvgTemparature;
        machineData.systemMinTemparature = systemMinTemparature;
        return machineData;
    }

    /**
     * @param data
     * @return
     */
    public static BmsData fromData(Integer[] data) {
        BmsData machineData = new BmsData();
        machineData.highestCellVoltage = ((data[0] << 8) + data[1]) * 1.5d;
        machineData.lowestCellVoltage = ((data[2] << 8) + data[3]) * 1.5d;
        machineData.systemMaxTemparature = ((data[4] << 8) + data[5]) * 0.1d;
        machineData.systemAvgTemparature = ((data[6] << 8) + data[7]) * 0.1d;
        machineData.systemMinTemparature = ((data[8] << 8) + data[9]) * 0.1d;
        return machineData;
    }

    public double getHighestCellVoltage() {
        return highestCellVoltage;
    }

    public double getLowestCellVoltage() {
        return lowestCellVoltage;
    }

    public double getSystemMaxTemparature() {
        return systemMaxTemparature;
    }

    public double getSystemAvgTemparature() {
        return systemAvgTemparature;
    }

    public double getSystemMinTemparature() {
        return systemMinTemparature;
    }

    @Override
    public String toString() {
        return "BmsData [highestCellVoltage=" + highestCellVoltage + ", lowestCellVoltage=" + lowestCellVoltage +
                ", systemMaxTemparature=" + systemMaxTemparature + ", systemAvgTemparature=" + systemAvgTemparature +
                ", systemMinTemparature=" + systemMinTemparature + "]";
    }
}
