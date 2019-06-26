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

import java.util.EnumSet;
import java.util.Map;

import com.ghgande.j2mod.modbus.ModbusException;

import nl.technolution.sunny.solaredge.sunspec.ESolarEdgeRegister;

/**
 * 
 */
public interface IModbusSession {
    /**
     * @throws ModbusException
     */
    void open() throws ModbusException;

    /**
     * @throws ModbusException
     */
    void close() throws ModbusException;

    /**
     * @return
     */
    boolean isOpen();

    /**
     * @param register
     * @param type
     * @return
     * @throws ModbusException
     */
    <T> T readRegister(ESolarEdgeRegister register, Class<T> type) throws ModbusException;

    /**
     * @param registers
     * @return
     */
    Map<ESolarEdgeRegister, SolarEdgeValue<?>> readRegisters(EnumSet<ESolarEdgeRegister> registers)
            throws ModbusException;

    /**
     * @param registers
     * @return
     */
    Map<ESolarEdgeRegister, SolarEdgeValue<?>> readMultipleRegisters(EnumSet<ESolarEdgeRegister> registers)
            throws ModbusException;

    /**
     * @param register
     * @param value
     * @throws ModbusException
     */
    void writeRegister(ESolarEdgeRegister register, Object value) throws ModbusException;
}
