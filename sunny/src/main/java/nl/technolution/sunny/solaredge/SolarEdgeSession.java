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
package nl.technolution.sunny.solaredge;

import java.net.InetAddress;
import java.util.EnumSet;
import java.util.Map;

import com.ghgande.j2mod.modbus.ModbusException;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.sunny.solaredge.sunspec.ESolarEdgeRegister;

/**
 * Gives access to SolarEdge device info in a convenient way (abstracting modbus registers)
 */
public class SolarEdgeSession implements ISolarEdgeSession {
    private static final Logger LOG = Log.getLogger();
    private IModbusSession session;

    /**
     * @param inetAddress IP address for Modbus connection
     * @param port Port for Modbus connection
     * @param deviceId Modbus device ID (also known as Device Address)
     */
    public void init(InetAddress inetAddress, int port, int deviceId) {
        this.session = new ModbusSession(inetAddress, port, deviceId);
        try {
            LOG.info("Connected to modbus device {} {} with S/N {}", getManufacturer().trim(), getModel().trim(),
                    getSerialNumber());
        } catch (ModbusException e) {
            throw new RuntimeException("Error communicating with modbus device:", e);
        }
    }

    @Override
    public double getInverterPower() throws ModbusException {
        Map<ESolarEdgeRegister, SolarEdgeValue<?>> values = session
                .readMultipleRegisters(EnumSet.of(ESolarEdgeRegister.I_AC_POWER, ESolarEdgeRegister.I_AC_POWER_SF));
        short power = (Short)values.get(ESolarEdgeRegister.I_AC_POWER).getValue();
        short scale = (Short)values.get(ESolarEdgeRegister.I_AC_POWER_SF).getValue();
        return power * Math.pow(10.0, scale);
    }

    /**
     * @return Manufacturer
     * @throws ModbusException
     */
    public String getManufacturer() throws ModbusException {
        return session.readRegister(ESolarEdgeRegister.C_MANUFACTURER, String.class);
    }

    /**
     * @return Model
     * @throws ModbusException
     */
    public String getModel() throws ModbusException {
        return session.readRegister(ESolarEdgeRegister.C_MODEL, String.class);
    }

    /**
     * @return SerialNumber
     * @throws ModbusException
     */
    public String getSerialNumber() throws ModbusException {
        return session.readRegister(ESolarEdgeRegister.C_SERIALNUMBER, String.class);
    }

    @Override
    public void stop() {
        try {
            session.close();
            LOG.info("Session stopped.");
        } catch (ModbusException e) {
            // Stopping anyway....
            LOG.warn("Error stopping modbus session: ", e);
        }
    }
}
