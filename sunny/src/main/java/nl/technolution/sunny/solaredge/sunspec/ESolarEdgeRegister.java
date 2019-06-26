/*
 (C) COPYRIGHT 2016 TECHNOLUTION BV, GOUDA NL
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
package nl.technolution.sunny.solaredge.sunspec;

import java.util.EnumSet;

/**
 * Enum representing registers of a SolarEdge inverter
 */
public enum ESolarEdgeRegister {
    /**
     * SunSpec defined registers These registers have an offset of -1, this offset is already applied (For example,
     * according to the documentation C_SUNSPEC_ID starts at address 40001)
     */
    C_SUNSPEC_ID((short)40000, (short)2, EModbusDataType.UINT32, ESunSpecUnit.NA),
    C_SUNSPEC_DID((short)40002, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    C_SUNSPEC_LENGTH((short)40003, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    C_MANUFACTURER((short)40004, (short)16, EModbusDataType.STRING, ESunSpecUnit.NA),
    C_MODEL((short)40020, (short)16, EModbusDataType.STRING, ESunSpecUnit.NA),
    C_VERSION((short)40044, (short)8, EModbusDataType.STRING, ESunSpecUnit.NA),
    C_SERIALNUMBER((short)40052, (short)16, EModbusDataType.STRING, ESunSpecUnit.NA),
    C_DEVICEADDRESS((short)40068, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),

    I_AC_CURRENT((short)40071, (short)1, EModbusDataType.UINT16, ESunSpecUnit.A),
    I_AC_CURRENT_A((short)40072, (short)1, EModbusDataType.UINT16, ESunSpecUnit.A),
    I_AC_CURRENT_B((short)40073, (short)1, EModbusDataType.UINT16, ESunSpecUnit.A),
    I_AC_CURRENT_C((short)40074, (short)1, EModbusDataType.UINT16, ESunSpecUnit.A),
    I_AC_CURRENT_SF((short)40075, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_AC_VOLTAGE_AB((short)40076, (short)1, EModbusDataType.UINT16, ESunSpecUnit.V),
    I_AC_VOLTAGE_BC((short)40077, (short)1, EModbusDataType.UINT16, ESunSpecUnit.V),
    I_AC_VOLTAGE_CA((short)40078, (short)1, EModbusDataType.UINT16, ESunSpecUnit.V),
    I_AC_VOLTAGE_AN((short)40079, (short)1, EModbusDataType.UINT16, ESunSpecUnit.V),
    I_AC_VOLTAGE_BN((short)40080, (short)1, EModbusDataType.UINT16, ESunSpecUnit.V),
    I_AC_VOLTAGE_CN((short)40081, (short)1, EModbusDataType.UINT16, ESunSpecUnit.V),
    I_AC_VOLTAGE_SF((short)40082, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_AC_POWER((short)40083, (short)1, EModbusDataType.INT16, ESunSpecUnit.W),
    I_AC_POWER_SF((short)40084, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_AC_FREQUENCY((short)40085, (short)1, EModbusDataType.UINT16, ESunSpecUnit.Hz),
    I_AC_FREQUENCY_SF((short)40086, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_AC_VA((short)40087, (short)1, EModbusDataType.INT16, ESunSpecUnit.VA),
    I_AC_VA_SF((short)40088, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_AC_VAR((short)40089, (short)1, EModbusDataType.INT16, ESunSpecUnit.VAR),
    I_AC_VAR_SF((short)40090, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_AC_PF((short)40091, (short)1, EModbusDataType.INT16, ESunSpecUnit.PCT),
    I_AC_PF_SF((short)40092, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    // I_AC_ENERGY_WH has type acc32 which is not supported, it is interpreted as uint32 instead
    I_AC_ENERGY_WH((short)40093, (short)2, EModbusDataType.UINT32, ESunSpecUnit.Wh),
    I_AC_ENERGY_WH_SF((short)40095, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_DC_CURRENT((short)40096, (short)1, EModbusDataType.UINT16, ESunSpecUnit.A),
    I_DC_CURRENT_SF((short)40097, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_DC_VOLTAGE((short)40098, (short)1, EModbusDataType.UINT16, ESunSpecUnit.V),
    I_DC_VOLTAGE_SF((short)40099, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_DC_POWER((short)40100, (short)1, EModbusDataType.INT16, ESunSpecUnit.W),
    I_DC_POWER_SF((short)40101, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_TEMP_SINK((short)40103, (short)1, EModbusDataType.INT16, ESunSpecUnit.C),
    I_TEMP_SINK_SF((short)40106, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),

    I_STATUS((short)40107, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    I_STATUS_VENDOR((short)40108, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),

    /**
     * SolarEdge defined registers, these registers do not have an offset
     */
    EXPORT_CONTROL((short)0xE000, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    STORAGE_CONTROL_MODE((short)0xE004, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    STORAGE_AC_CHARGE_POLICY((short)0xE005, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    STORAGE_AC_CHARGE_LIMIT((short)0xE006, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.KWh),
    STORAGE_BACKUP_RESERVED_SETTING((short)0xE008, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.PCT),
    STORAGE_CHARGE_DISCHARGE_DEFAULT_MODE((short)0xE00A, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    REMOTE_CONTROL_COMMAND_TIMEOUT((short)0xE00B, (short)2, EModbusDataType.UINT32, ESunSpecUnit.SEC),
    REMOTE_CONTROL_COMMAND_MODE((short)0xE00D, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    REMOTE_CONTROL_CHARGE_LIMIT((short)0xE00E, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.W),
    REMOTE_CONTROL_DISCHARGE_LIMIT((short)0xE010, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.W),

    BATTERY_1_MANUFACTURER_NAME((short)0xE100, (short)16, EModbusDataType.STRING, ESunSpecUnit.NA),
    BATTERY_1_MODEL((short)0xE110, (short)16, EModbusDataType.STRING, ESunSpecUnit.NA),
    BATTERY_1_FIRMWARE_VERSION((short)0xE120, (short)16, EModbusDataType.STRING, ESunSpecUnit.NA),
    BATTERY_1_SERIAL_NUMBER((short)0xE130, (short)16, EModbusDataType.STRING, ESunSpecUnit.NA),
    BATTERY_1_DEVICE_ID((short)0xE140, (short)1, EModbusDataType.UINT16, ESunSpecUnit.NA),
    // E141 reserved
    BATTERY_1_RATED_ENERGY((short)0xE142, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_MAX_CHARGE_CONTINUES_POWER((short)0xE144, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_MAX_DISCHARGE_CONTINUES_POWER((short)0xE146, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_MAX_CHARGE_PEAK_POWER((short)0xE148, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_MAX_DISCHARGE_PEAK_POWER((short)0xE14A, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    // E14C reserved
    BATTERY_1_AVERAGE_TEMPERATURE((short)0xE16C, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.C),
    BATTERY_1_MAX_TEMPERATURE((short)0xE16E, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.C),
    BATTERY_1_INSTANTANEOUS_VOLTAGE((short)0xE170, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.V),
    BATTERY_1_INSTANTANEOUS_CURRENT((short)0xE172, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.A),
    BATTERY_1_INSTANTANEOUS_POWER((short)0xE174, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.W),
    BATTERY_1_LIFETIME_EXPORT_ENERGY_COUNTER((short)0xE176, (short)4, EModbusDataType.UINT64, ESunSpecUnit.Wh),
    BATTERY_1_LIFETIME_IMPORT_ENERGY_COUNTER((short)0xE17A, (short)4, EModbusDataType.UINT64, ESunSpecUnit.Wh),
    BATTERY_1_MAX_ENERGY((short)0xE17E, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_AVAILABLE_ENERGY((short)0xE180, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_STATE_OF_HEALTH((short)0xE182, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_STATE_OF_ENERGY((short)0xE184, (short)2, EModbusDataType.FLOAT32, ESunSpecUnit.Wh),
    BATTERY_1_STATUS((short)0xE186, (short)2, EModbusDataType.UINT32, ESunSpecUnit.NA),
    BATTERY_1_STATUS_INTERNAL((short)0xE188, (short)2, EModbusDataType.UINT32, ESunSpecUnit.NA),
    BATTERY_1_EVENT_LOG((short)0xE18A, (short)8, EModbusDataType.UINT16, ESunSpecUnit.NA),
    BATTERY_1_EVENT_LOG_INTERNAL((short)0xE192, (short)8, EModbusDataType.UINT16, ESunSpecUnit.NA),

    POWER_CONTROL_COMMIT((short)0xF100, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),
    POWER_CONTROL_RESET((short)0xF101, (short)1, EModbusDataType.INT16, ESunSpecUnit.NA),
    POWER_CONTROL_REACTIVE_POWER_CONFIG((short)0xF104, (short)2, EModbusDataType.INT32, ESunSpecUnit.NA),
    POWER_CONTROL_ADVANCED_ENABLE((short)0xF142, (short)2, EModbusDataType.INT32, ESunSpecUnit.NA);

    private static final short BASE_CORRECTION = 0;

    /** Set with ranges of continuous registers which can be read in a single multiple read */
    @SuppressWarnings("unchecked")
    public static final EnumSet<ESolarEdgeRegister>[] SEGMENTS = new EnumSet[] {
        EnumSet.range(C_SUNSPEC_ID, C_DEVICEADDRESS),
        EnumSet.range(I_AC_CURRENT, I_STATUS_VENDOR),
        EnumSet.range(EXPORT_CONTROL, REMOTE_CONTROL_DISCHARGE_LIMIT),
        EnumSet.range(BATTERY_1_MANUFACTURER_NAME, BATTERY_1_DEVICE_ID),
        EnumSet.range(BATTERY_1_RATED_ENERGY, BATTERY_1_MAX_DISCHARGE_PEAK_POWER),
        EnumSet.range(BATTERY_1_AVERAGE_TEMPERATURE, BATTERY_1_EVENT_LOG_INTERNAL),
        EnumSet.range(POWER_CONTROL_COMMIT, POWER_CONTROL_ADVANCED_ENABLE) };

    private final short address;
    private final short size;
    private final EModbusDataType type;
    private final ESunSpecUnit unit;

    ESolarEdgeRegister(short address, short size, EModbusDataType type, ESunSpecUnit unit) {
        this.address = (short)(address - BASE_CORRECTION);
        this.size = size;
        this.type = type;
        this.unit = unit;
    }

    public short getAddress() {
        return address;
    }

    public short getSize() {
        return size;
    }

    public EModbusDataType getType() {
        return type;
    }

    public ESunSpecUnit getUnit() {
        return unit;
    }

    /**
     * valueOf with null support
     * 
     * @param name enum name
     * @return enum instance or null
     */
    public static ESolarEdgeRegister getByName(String name) {
        for (ESolarEdgeRegister register : ESolarEdgeRegister.values()) {
            if (register.name().contentEquals(name)) {
                return register;
            }
        }
        return null;
    }
}
