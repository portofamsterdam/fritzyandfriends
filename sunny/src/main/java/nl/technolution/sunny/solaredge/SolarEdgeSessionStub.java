/*
 (C) COPYRIGHT 2019 TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.solaredge;

import com.ghgande.j2mod.modbus.ModbusException;

/**
 * Stub for SolarEdgeSession
 */
public class SolarEdgeSessionStub implements ISolarEdgeSession {
    private double inverterPower = 0;

    @Override
    public double getInverterPower() throws ModbusException {
        return inverterPower;
    }

    @Override
    public void stop() {
        // Nothing to stop in this stub
    }

    public void setInverterPower(double inverterPower) {
        this.inverterPower = inverterPower;
    }
}
