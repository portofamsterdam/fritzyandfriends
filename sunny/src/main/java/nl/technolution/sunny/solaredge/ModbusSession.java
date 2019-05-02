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
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Map;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.util.ModbusUtil;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jersey.repackaged.com.google.common.collect.Maps;
import nl.technolution.sunny.solaredge.sunspec.EModbusDataType;
import nl.technolution.sunny.solaredge.sunspec.ESolarEdgeRegister;
import nl.technolution.sunny.solaredge.sunspec.UnsignedInteger;
import nl.technolution.sunny.solaredge.sunspec.UnsignedLong;
import nl.technolution.sunny.solaredge.sunspec.UnsignedShort;

public class ModbusSession implements IModbusSession {

	private static final Logger LOG = LoggerFactory.getLogger(ModbusSession.class);
	
	private final Object lock = new Object();
	private final TCPMasterConnection connection;

	private boolean isConnected = false;
    private boolean stopped = false;
	
	public ModbusSession(InetAddress address, int port) {
		synchronized (lock) {
			this.connection = new TCPMasterConnection(address);
			this.connection.setPort(port);
		}
	}

    public int getPort() {
        return connection.getPort();
    }

    public InetAddress getIpAddress() {
        return connection.getAddress();
    }

	public void open() throws ModbusException {
		synchronized (lock) {
			try {
				connection.connect();
				// See MRV-661, writing the control mode register takes more than 2s so set default timeout to 5s
                connection.setTimeout(5000);
				isConnected = true;
			} catch (Exception e) {
				throw new ModbusException(e.getMessage(), e);
			}
		}
	}

	public void close() {
		synchronized (lock) {
			isConnected = false;
			connection.close();
		}
	}

    public void stop() {
        stopped = true;
        close();
    }

	public boolean isOpen() {
		synchronized (lock) {
			return connection.isConnected() && isConnected;
		}
	}

    private void reOpen() throws ModbusException {
        if (stopped) {
            throw new ModbusException("Session stopped, can't reopen!");
        } else {
            close();
            open();
        }
    }

    public byte[] readSlave(int startAddress, int words) throws ModbusException {

        ModbusTransaction transaction = SolarEdgeUtils.createTransaction(connection);
        ReadMultipleRegistersRequest request = SolarEdgeUtils.createReadRequest();

        request.setReference(startAddress);
        request.setTransactionID(transaction.getTransactionID());
        transaction.setRequest(request);
        try {
            transaction.execute();
        } catch (ModbusException e) {
            close();
            throw e;
        }

        byte[] bytes = SolarEdgeUtils
                .registersToBytes(((ReadMultipleRegistersResponse)transaction.getResponse()).getRegisters());
        if ((bytes.length) != (words * 2)) {
            close();
            throw new ModbusException(
                    String.format("Read %d bytes instead of expected %d bytes", bytes.length, words * 2));
        }
        for (int i = 0; i < words; i++) {
            LOG.trace("{}", String.format("Read 0x%04X: 0x%04X", (startAddress + i) & 0xFFFF,
                    ModbusUtil.registerToShort(ArrayUtils.subarray(bytes, i * 2, (i * 2) + 2))));
        }
        return bytes;
    }

	public byte[] readRegister(short startAddress, int words)
			throws ModbusException {
		synchronized (lock) {
			if (!isOpen()) {
                reOpen();
			}
			ModbusTransaction transaction = SolarEdgeUtils.createTransaction(connection);
			ReadMultipleRegistersRequest request = SolarEdgeUtils.createReadRequest();
			request.setReference(startAddress);
			request.setWordCount(words);
            request.setTransactionID(transaction.getTransactionID());
			transaction.setRequest(request);
			try {
				transaction.execute();
			} catch(ModbusException e) {
				close();
				throw e;
			}

			byte[] bytes = SolarEdgeUtils.registersToBytes(((ReadMultipleRegistersResponse) transaction.getResponse()).getRegisters());
			if ((bytes.length) != (words * 2)) {
				close();
				throw new ModbusException(String.format("Read %d bytes instead of expected %d bytes", bytes.length, words * 2));
			}
			for (int i = 0; i < words; i++) {
				LOG.trace("{}", String.format("Read 0x%04X: 0x%04X", (startAddress + i) & 0xFFFF, ModbusUtil.registerToShort(ArrayUtils.subarray(bytes, i * 2, (i * 2) + 2))));
			}
			return bytes;
		}
	}
	
	public byte[] readRegister(short startAddress, int words, int unitId)
			throws ModbusException {
		synchronized (lock) {
			if (!isOpen()) {
                reOpen();
			}
			ModbusTransaction transaction = SolarEdgeUtils.createTransaction(connection);
			transaction.setRetries(0);
			ReadMultipleRegistersRequest request = SolarEdgeUtils.createReadRequest();
			request.setReference(startAddress);
			request.setWordCount(words);
			request.setUnitID(unitId);
            request.setTransactionID(transaction.getTransactionID());
			transaction.setRequest(request);
			try {
				transaction.execute();
			} catch(ModbusException e) {
				close();
				throw e;
			}

			byte[] bytes = SolarEdgeUtils.registersToBytes(((ReadMultipleRegistersResponse) transaction.getResponse()).getRegisters());
			if ((bytes.length) != (words * 2)) {
				close();
				throw new ModbusException(String.format("Read %d bytes instead of expected %d bytes", bytes.length, words * 2));
			}
			for (int i = 0; i < words; i++) {
				LOG.trace("{}", String.format("Read 0x%04X: 0x%04X", (startAddress + i) & 0xFFFF, ModbusUtil.registerToShort(ArrayUtils.subarray(bytes, i * 2, (i * 2) + 2))));
			}
			return bytes;
		}
	}

	public <T> T readRegister(ESolarEdgeRegister register, Class<T> type)
			throws ModbusException {
		return convertType(readRegister(register.getAddress(), register.getSize()), type);
	}
	

	public <T> T readRegister(ESolarEdgeRegister register, Class<T> type, int unitId)
			throws ModbusException {
		return convertType(readRegister(register.getAddress(), register.getSize(), unitId), type);
	}
	
	public Map<ESolarEdgeRegister, SolarEdgeValue<?>> readRegisters(EnumSet<ESolarEdgeRegister> registers) throws ModbusException {
		Map<ESolarEdgeRegister, SolarEdgeValue<?>> values = Maps.newHashMap();
		for (ESolarEdgeRegister register : registers) {
			Class<?> type = register.getType().getType();
			//TODO PVE fix warning
			values.put(register, new SolarEdgeValue(type, readRegister(register, type)));
		}
		return values;
	}
	
	public Map<ESolarEdgeRegister, SolarEdgeValue<?>> readMultipleRegisters(EnumSet<ESolarEdgeRegister> registers) throws ModbusException {
		Map<ESolarEdgeRegister, SolarEdgeValue<?>> values = Maps.newHashMap();
		
		// Retrieve the lowest & highest register from the enumset.
		ESolarEdgeRegister lowestRegister = null;
		ESolarEdgeRegister highestRegister = null;
		for (ESolarEdgeRegister register : registers) {
			if (lowestRegister == null || lowestRegister.getAddress() > register.getAddress()) {
				lowestRegister = register;
			}
			if (highestRegister == null || highestRegister.getAddress() < register.getAddress()) {
				highestRegister = register;
			}
		}
		
		if (lowestRegister == null || highestRegister == null) {
			return values;
		}
		
		// Calculate the number of words to read
		int words = (highestRegister.getAddress() + highestRegister.getSize()) - lowestRegister.getAddress();
		byte[] data = readRegister(lowestRegister.getAddress(), words);
		
		for (ESolarEdgeRegister register : registers) {
			// Calculate word address to byte address
			int address = (register.getAddress() - lowestRegister.getAddress()) * 2;
			// Calculate word length to byte length
			int length = register.getSize() * 2;
			byte[] bytes = ArrayUtils.subarray(data, address, address + length);
			
			Class<?> type = register.getType().getType();
			values.put(register, new SolarEdgeValue(type, convertType(bytes, type)));
		}
		
		return values;
	}

	public void writeRegister(short startAddress, byte[] data)
			throws ModbusException {
		synchronized (lock) {
			if (!isOpen()) {
                reOpen();
			}
			int words = data.length / 2;
			ModbusTransaction transaction = SolarEdgeUtils
					.createTransaction(connection);
			WriteMultipleRegistersRequest request = SolarEdgeUtils
					.createWriteRequest();
			request.setReference(startAddress);
			request.setDataLength(words);

			request.setRegisters(SolarEdgeUtils.bytesToRegisters(data));
            request.setTransactionID(transaction.getTransactionID());

			transaction.setRequest(request);
			
			for (int i = 0; i < words; i++) {
				LOG.trace("{}", String.format("Write 0x%04X: 0x%04X", (startAddress + i) & 0xFFFF, ModbusUtil.registerToShort(ArrayUtils.subarray(data, i * 2, (i * 2) + 2))));
			}
			
			try {
				transaction.execute();
			} catch(ModbusException e) {
				close();
				throw e;
			}
		}
	}

	public void writeRegister(ESolarEdgeRegister register, Object value) throws ModbusException {
		EModbusDataType dataType = register.getType();
		if (dataType.getType() == Float.class) {
			writeRegister(register.getAddress(), SolarEdgeUtils.modiconFloatToRegisters((Float) value));
  		} else if (dataType.getType() == Long.class) {
  			writeRegister(register.getAddress(), SolarEdgeUtils.SolarEdgeLongToRegisters((Long) value));
  		} else if (dataType.getType() == UnsignedLong.class) {
  			writeRegister(register.getAddress(), SolarEdgeUtils.SolarEdgeUnsignedLongToRegisters((UnsignedLong)value));
  		} else if (dataType.getType() == Integer.class) {
  			writeRegister(register.getAddress(), SolarEdgeUtils.SolarEdgeIntegerToRegisters((Integer) value));
  		} else if (dataType.getType() == UnsignedInteger.class) {
  			writeRegister(register.getAddress(), SolarEdgeUtils.SolarEdgeIntegerToRegisters(((UnsignedInteger) value).getSignedValue()));
  		} else if (dataType.getType() == Short.class) {
  			writeRegister(register.getAddress(), ModbusUtil.shortToRegister((Short) value));
  		} else if (dataType.getType() == UnsignedShort.class) {
  			writeRegister(register.getAddress(), ModbusUtil.shortToRegister(((UnsignedShort) value).getSignedValue()));
  		} else if (dataType.getType() == String.class) {
  			writeRegister(register.getAddress(), ((String) value).getBytes(Charset.forName("US-ASCII")));
  		} else {
  			throw new IllegalArgumentException("Unknown type: " + dataType.getType().getName());
  		}
	}
	
	public <T> T convertType(byte[] data, Class<T> type)
			throws ModbusException {
		if (type == Float.class) {
			return type.cast(SolarEdgeUtils.registersToModiconFloat(data));
		} else if (type == Long.class) {
			return type.cast(SolarEdgeUtils.registersToSolarEdgeLong(data));
		} else if (type == UnsignedLong.class) {
	          return type.cast(SolarEdgeUtils.registersToSolarEdgeUnsignedLong(data));
		} else if (type == Integer.class) {
			return type.cast(ModbusUtil.registersToInt(data));
		} else if (type == UnsignedInteger.class) {
			return type.cast(new UnsignedInteger(SolarEdgeUtils.registersToSolarEdgeInteger(data)));
		} else if (type == Short.class) {
			return type.cast(ModbusUtil.registerToShort(data));
		} else if (type == UnsignedShort.class) {
			return type.cast(new UnsignedShort(ModbusUtil.registerToShort(data)));
		} else if (type == String.class) {
			return type.cast(SolarEdgeUtils.registersToString(data));
		}

		throw new IllegalArgumentException("Unknown type: " + type.getName());
	}
}
