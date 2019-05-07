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

import java.nio.ByteBuffer;

/**
 * 
 */
public final class MeterInfo {

    // 0.1 V Meter1 Voltage 2 Bytes Dec(hex(0)hex(1))
    private double voltage;
    // 0.01A Meter1 Current 2 Bytes Dec(hex(2)hex(3))
    private double current;
    // 10W Meter1 Active power 2 Bytes Dec(hex(4)hex(5))
    private int activePower;
    // 10VAR Meter1 Reactive power 2 Bytes Dec(hex(6)hex(7))
    private int reactivePower;
    // 10VA Meter1 Apparent power 2 Bytes Dec(hex(8)hex(9))
    private int apparentPower;
    // 0.001 Meter1 Power factor 2 Bytes Dec(hex(10)hex(11))
    private double powerFactor;
    // 0.01Hz Meter1 Frequency 2 Bytes Dec(hex(12)hex(13))
    private double frequency;
    // 10W Meter1 Phase1 Active power 2 Bytes Dec(hex(14)hex(15))
    private int phase1activePower;
    // 10W Meter1 Phase2 Active power 2 Bytes Dec(hex(16)hex(17))
    private int phase2activePower;
    // 10W Meter1 Phase3 Active power 2 Bytes Dec(hex(18)hex(19))
    private int phase3activePower;

    private MeterInfo() {
        // 
    }

    /**
     * @param voltage
     * @param current
     * @param activePower
     * @param reactivePower
     * @param apparentPower
     * @param powerFactor
     * @param frequency
     * @param phase1activePower
     * @param phase2activePower
     * @param phase3activePower
     * @return
     */
    public static MeterInfo build(double voltage, double current, int activePower, int reactivePower, int apparentPower,
            double powerFactor, double frequency, int phase1activePower, int phase2activePower, int phase3activePower) {
        MeterInfo info = new MeterInfo();
        info.voltage = voltage;
        info.current = current;
        info.activePower = activePower;
        info.reactivePower = reactivePower;
        info.apparentPower = apparentPower;
        info.powerFactor = powerFactor;
        info.frequency = frequency;
        info.phase1activePower = phase1activePower;
        info.phase2activePower = phase2activePower;
        info.phase3activePower = phase3activePower;
        return info;
    }

    /**
     * String of data comma seperated
     * 
     * @param data to parse
     * @return instance
     */
    public static MeterInfo fromData(String data) {
        ByteBuffer buffer = ByteBuffer.allocate(20);

        // Write all values as decimal values to buffer
        for (String decimalValue : data.split(",")) {
            byte b = (byte)Integer.parseInt(decimalValue);
            buffer.put(b);
        }

        byte[] meterInfoData = buffer.array();
        buffer = ByteBuffer.wrap(meterInfoData);

        MeterInfo info = new MeterInfo();
        info.voltage = buffer.getShort() * 0.1d;
        info.current = buffer.getShort() * 0.01d;
        info.activePower = buffer.getShort() * 10;
        info.reactivePower = buffer.getShort() * 10;
        info.apparentPower = buffer.getShort() * 10;
        info.powerFactor = buffer.getShort() * 0.001d;
        info.frequency = buffer.getShort() * 0.01d;
        info.phase1activePower = buffer.getShort() * 10;
        info.phase2activePower = buffer.getShort() * 10;
        info.phase3activePower = buffer.getShort() * 10;
        return info;
    }

    public double getVoltage() {
        return voltage;
    }

    public double getCurrent() {
        return current;
    }

    public int getActivePower() {
        return activePower;
    }

    public int getReactivePower() {
        return reactivePower;
    }

    public int getApparentPower() {
        return apparentPower;
    }

    public double getPowerFactor() {
        return powerFactor;
    }

    public double getFrequency() {
        return frequency;
    }

    public int getPhase1activePower() {
        return phase1activePower;
    }

    public int getPhase2activePower() {
        return phase2activePower;
    }

    public int getPhase3activePower() {
        return phase3activePower;
    }
}
