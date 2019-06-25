package nl.technolution.sunny.solaredge;

import java.net.InetAddress;
import java.util.EnumSet;
import java.util.Map;

import com.ghgande.j2mod.modbus.ModbusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.technolution.sunny.solaredge.sunspec.ESolarEdgeRegister;
import nl.technolution.sunny.solaredge.sunspec.UnsignedInteger;
import nl.technolution.sunny.solaredge.sunspec.UnsignedShort;

public class SolarEdgeSession {
    private static final Logger LOG = LoggerFactory.getLogger(SolarEdgeSession.class);

	private static final short REMOTE_CONTROL_COMMAND_MODE_CHARGE = 3;
	private static final short REMOTE_CONTROL_COMMAND_MODE_DISCHARGE = 4;

	private final IModbusSession session;

	public SolarEdgeSession(IModbusSession session) {
		this.session = session;
	}

	public String getSerialNumber() throws ModbusException {
			return session
					.readRegister(
							ESolarEdgeRegister.C_SERIALNUMBER,
							String.class);
	}

    public String getBatteryManufacturerName() throws ModbusException {
        return session.readRegister(ESolarEdgeRegister.BATTERY_1_MANUFACTURER_NAME, String.class);
    }

	public float getStateOfEnergy() throws ModbusException {
			return session.readRegister(
					ESolarEdgeRegister.BATTERY_1_STATE_OF_ENERGY,
					Float.class);
	}

	public float getAvailableEnergy() throws ModbusException {
			return session.readRegister(
					ESolarEdgeRegister.BATTERY_1_AVAILABLE_ENERGY,
					Float.class);
	}

	public double getGridFrequency() throws ModbusException {			
			Map<ESolarEdgeRegister, SolarEdgeValue<?>> values = session.readMultipleRegisters(EnumSet.of(ESolarEdgeRegister.I_AC_FREQUENCY, ESolarEdgeRegister.I_AC_FREQUENCY_SF));
			UnsignedShort frequency = (UnsignedShort) values.get(ESolarEdgeRegister.I_AC_FREQUENCY).getValue();
			short scale = (Short) values.get(ESolarEdgeRegister.I_AC_FREQUENCY_SF).getValue();
			return frequency.getValue() * Math.pow(10.0, scale);
	}

	public double getInverterPower() throws ModbusException {
		Map<ESolarEdgeRegister, SolarEdgeValue<?>> values = session.readMultipleRegisters(EnumSet.of(ESolarEdgeRegister.I_AC_POWER, ESolarEdgeRegister.I_AC_POWER_SF));
		short power = (Short) values.get(ESolarEdgeRegister.I_AC_POWER).getValue();
		short scale = (Short) values.get(ESolarEdgeRegister.I_AC_POWER_SF).getValue();
		return power * Math.pow(10.0, scale);
	}
	
	public double getInverterDcPower() throws ModbusException {
		Map<ESolarEdgeRegister, SolarEdgeValue<?>> values = session.readMultipleRegisters(EnumSet.of(ESolarEdgeRegister.I_DC_POWER, 
				ESolarEdgeRegister.I_DC_POWER_SF));
		short power = (Short) values.get(ESolarEdgeRegister.I_DC_POWER).getValue();
		short scale = (Short) values.get(ESolarEdgeRegister.I_DC_POWER_SF).getValue();
		return power * Math.pow(10.0, scale);
	}

	public double getBatteryPower() throws ModbusException {
			return session.readRegister(
					ESolarEdgeRegister.BATTERY_1_INSTANTANEOUS_POWER,
					Float.class);
	}

	public double getBatteryTemperature() throws ModbusException {
			return session.readRegister(
					ESolarEdgeRegister.BATTERY_1_AVERAGE_TEMPERATURE,
					Float.class);
	}
	
	public Map<ESolarEdgeRegister, SolarEdgeValue<?>> getRegisters(EnumSet<ESolarEdgeRegister> registers) throws ModbusException {
        return session.readMultipleRegisters(registers);
	}

	public void charge(float power, int seconds) throws ModbusException {
			configureRemoteControl();

			session.writeRegister(
					ESolarEdgeRegister.REMOTE_CONTROL_COMMAND_MODE,
					new UnsignedShort(REMOTE_CONTROL_COMMAND_MODE_CHARGE));
			session.writeRegister(
					ESolarEdgeRegister.REMOTE_CONTROL_CHARGE_LIMIT, power);
			session.writeRegister(
					ESolarEdgeRegister.REMOTE_CONTROL_COMMAND_TIMEOUT,
					new UnsignedInteger(seconds));
	}

	public void discharge(float power, int seconds) throws ModbusException {
			configureRemoteControl();

			session.writeRegister(
					ESolarEdgeRegister.REMOTE_CONTROL_COMMAND_MODE,
					new UnsignedShort(REMOTE_CONTROL_COMMAND_MODE_DISCHARGE));
			session.writeRegister(
					ESolarEdgeRegister.REMOTE_CONTROL_DISCHARGE_LIMIT, power);
			session.writeRegister(
					ESolarEdgeRegister.REMOTE_CONTROL_COMMAND_TIMEOUT,
					new UnsignedInteger(seconds));
	}

	private void configureRemoteControl() throws ModbusException {
        // MRV-661: setting export Control to disabled takes long (about 2.2s), check if it is needed:
        UnsignedShort exportControl = session.readRegister(
                ESolarEdgeRegister.EXPORT_CONTROL, UnsignedShort.class);
        if (exportControl.getValue() == 0) {
            LOG.debug("Export control is already disabled, no change needed.");
        } else {
            session.writeRegister(ESolarEdgeRegister.EXPORT_CONTROL,
                    new UnsignedShort((short)0));
        }
		session.writeRegister(ESolarEdgeRegister.STORAGE_CONTROL_MODE,
				new UnsignedShort((short)4));
		session.writeRegister(
				ESolarEdgeRegister.STORAGE_AC_CHARGE_POLICY,
				new UnsignedShort((short)1));
		session.writeRegister(
				ESolarEdgeRegister.STORAGE_CHARGE_DISCHARGE_DEFAULT_MODE,
				new UnsignedShort((short)0));
	}

	public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getByName("192.168.8.240");
		int port = 502;

        ModbusSession modbusSession = new ModbusSession(address, port, 2);
		SolarEdgeSession session = new SolarEdgeSession(modbusSession);

		try {
			modbusSession.open();
//			short addr = ESolarEdgeRegister.I_STATUS.getAddress();
			int addr = 40052;
            System.out.println("" + addr + ":" +
                    modbusSession.convertType(modbusSession.readSlave(addr, 32), String.class));
		} finally {
            modbusSession.close();
		}
	
		
	}
}
