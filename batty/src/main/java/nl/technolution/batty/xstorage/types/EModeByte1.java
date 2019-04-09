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
public enum EModeByte1 {
    // Standby 0x00 0xXX Inverter does not working due to user’s command or system failure.
    STANDBY(0),
    // Waiting 0x01 0xXX Receive the “Power On” command and waiting the connection check before grid-tied operation.
    // (The countdown timing is defined by grid regulation.)
    WAITING(1),
    // Basic 0x02 0xXX Inverter starts working but no other command input. Grid Tied Mode 1 (Grid to Loads)
    BASIS(2),
    // Charge 0x03 0xXX Grid Tied Mode 4 (PV, Charging) Grid Tied Mode 5 (No PV, Charging)
    CHARGE(3),
    // Discharge 0x04 0xXX Grid Tied Mode 2 (Normal Discharge) Grid Tied Mode 3 (No PV, Discharge)
    DISCHARGE(4),
    // Backup 0x05 0xXX Off-Grid Mode 1 (PV + Battery) Off-Grid Mode 2 (Battery)
    BACKUP(5),
    // No Grid Injection 0x06 0xXX This mode (also called zero export) will allow to the consumer not to inject power
    // into the grid by charging the battery when there is solar energy available and consumption is low or by
    // curtailing PV production when there is no loads and battery is fully charged.
    NO_GRID_INJECTION(6),
    // Maximize Auto-Consumption 0x07 0xXX In this mode, the battery will auto charge and discharge in order to reduce
    // the energy consume from the grid. PV power will be maximized in any situation.
    MAXIMIZE_AUTO_CONSUMPTION(7),
    // Peak Shaving 0x08 0xXX This mode will enable the customer to make sure that the electricity consumption of the
    // house is below a defined Peak Power consumption.
    PEAK_SAVING(8),
    // Minimum Cost of Electricity 0x09 0xXX This mode will enable the consumer to charge the battery when the cost of
    // electricity is low and discharge when the cost of electricity is high.
    MINIMUM_COST_OF_ELECTRICITY(9),
    // Frequency Regulation 0x0A 0xXX This mode is country dependent, the development of the frequency regulation mode
    // has been based on FFR (Firm Frequency Regulation) in UK. So the parameters can be different when applied to
    // another country, however the philosophy remains the same.
    FREQUENCY_REGULATION(10);

    private final int index;

    EModeByte1(int index) {
        this.index = index;
    }

    /**
     * Find mode for index
     * 
     * @param index to find
     * @return mode (byte 0)
     */
    public static EModeByte1 fromIndex(int index) {
        for (EModeByte1 mode0 : values()) {
            if (mode0.index == index) {
                return mode0;
            }
        }
        throw new IllegalArgumentException("unspecified index " + index);
    }

    public int getIndex() {
        return index;
    }
}
