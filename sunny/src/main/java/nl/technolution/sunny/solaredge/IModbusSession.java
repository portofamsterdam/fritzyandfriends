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

public interface IModbusSession {

	public void open() throws ModbusException;
	
	public void close() throws ModbusException;
	
	public boolean isOpen();

	public <T> T readRegister(ESolarEdgeRegister register, Class<T> type) throws ModbusException;

    public <T> T readRegister(ESolarEdgeRegister register, Class<T> type, int unitId) throws ModbusException;
	
	public Map<ESolarEdgeRegister, SolarEdgeValue<?>> readRegisters(EnumSet<ESolarEdgeRegister> registers) throws ModbusException;
	
	public Map<ESolarEdgeRegister, SolarEdgeValue<?>> readMultipleRegisters(EnumSet<ESolarEdgeRegister> registers) throws ModbusException;
	
	public void writeRegister(ESolarEdgeRegister register, Object value) throws ModbusException;
}
