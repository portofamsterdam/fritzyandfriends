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
package nl.technolution.batty.trader;

import java.time.Instant;

import com.google.common.base.Preconditions;

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.ActuatorInstruction;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionRevoke;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.Measurement.ElectricityMeasurement;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageUpdate;
import nl.technolution.protocols.efi.util.Efi;
import nl.technolution.protocols.efi.util.ICustomerEnergyManager;
import nl.technolution.protocols.efi.util.IResourceManager;

/**
 * Resource Manager of sunny
 */
public class BattyResourceManager implements IResourceManager {

    private final DeviceId deviceId;
    private final BattyResourceHelper helper;
    private final BattyController controller;

    private ICustomerEnergyManager<StorageRegistration, StorageUpdate> cem;

    public BattyResourceManager(DeviceId deviceId) {
        this.deviceId = deviceId;
        this.helper = new BattyResourceHelper(deviceId);
        this.controller = new BattyController();
    }

    @Override
    public void instruct(Instruction instruction) {
        Preconditions.checkArgument(instruction instanceof StorageInstruction, "Expected storage instruction");
        StorageInstruction storageInstruction = StorageInstruction.class.cast(instruction);
        storageInstruction.getActuatorInstructions().getActuatorInstruction().forEach(this::handleActuatorInstruction);
    }

    @Override
    public void instructionRevoke(InstructionRevoke instructionRevoke) {
        controller.stop();
    }

    private void handleActuatorInstruction(ActuatorInstruction instruction) {
        Preconditions.checkNotNull(instruction);
        Preconditions.checkArgument(instruction.getActuatorId() == BattyResourceHelper.ACTUATOR_ID);

        // NOTE: instruction start time not supported, always execute now
        EBattyInstruction battyInstruction = EBattyInstruction.fromRunningModeId(instruction.getRunningModeId());
        switch (battyInstruction) {
        case CHARGE:
            controller.charge(100d); // Always charge at full capacity
            break;
        case DISCHARGE:
            controller.discharge(100d); // always charge at full capacity
            break;
        default:
            throw new IllegalStateException("Unknown instruction " + battyInstruction);
        }
    }

    /**
     * Evaluate current state and update CEM.
     */
    public void evaluate() {
        cem.flexibilityUpdate(helper.getFlexibilityUpdate());
    }

    /**
     * Register to a Customer Energy Manager.
     * 
     * @param cem to register to
     */
    public void registerCustomerEnergyManager(BatteryNegotiator cem) {
        this.cem = cem;
        cem.flexibilityRegistration(helper.getRegistration());
        cem.flexibilityUpdate(helper.getStorageSystemDescription());
    }

    /**
     * Send measurement
     * 
     * @param power drawn from net
     */
    public void sendMeasurement(double power) {
        Measurement measurement = Efi.build(Measurement.class, deviceId);
        measurement.setMeasurementTimestamp(Efi.calendarOfInstant(Instant.now()));
        ElectricityMeasurement value = new ElectricityMeasurement();
        value.setPower(power);
        measurement.setElectricityMeasurement(value);
        cem.measurement(measurement);
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }
}
