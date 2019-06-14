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
package nl.technolution.fritzy.marketnegotiator;

import java.time.Instant;

import com.google.common.base.Preconditions;

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionRevoke;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.Measurement.ElectricityMeasurement;
import nl.technolution.protocols.efi.SequentialProfileInstruction;
import nl.technolution.protocols.efi.SequentialProfileInstructions;
import nl.technolution.protocols.efi.ShiftableInstruction;
import nl.technolution.protocols.efi.ShiftableRegistration;
import nl.technolution.protocols.efi.ShiftableUpdate;
import nl.technolution.protocols.efi.util.Efi;
import nl.technolution.protocols.efi.util.ICustomerEnergyManager;
import nl.technolution.protocols.efi.util.IResourceManager;

/**
 * Resource Manager of sunny
 */
public class FritzyResourceManager implements IResourceManager {

    private final DeviceId deviceId;
    private final FritzyResourceHelper helper;
    private final FritzyController controller;

    private ICustomerEnergyManager<ShiftableRegistration, ShiftableUpdate> cem;

    public FritzyResourceManager(DeviceId deviceId) {
        this.deviceId = deviceId;
        this.helper = new FritzyResourceHelper(deviceId);
        this.controller = new FritzyController();
    }

    @Override
    public void instruct(Instruction instruction) {
        Preconditions.checkArgument(instruction instanceof ShiftableInstruction, "Expected storage instruction");
        ShiftableInstruction shiftableInstruction = ShiftableInstruction.class.cast(instruction);
        SequentialProfileInstructions actuatorInstructions = shiftableInstruction.getSequentialProfileInstructions();
        actuatorInstructions.getSequentialProfileInstruction().forEach(this::handleActuatorInstruction);
    }

    @Override
    public void instructionRevoke(InstructionRevoke instructionRevoke) {
        controller.stop();
    }

    private void handleActuatorInstruction(SequentialProfileInstruction instruction) {

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
    public void registerCustomerEnergyManager(FritzyNegotiator cem) {
        this.cem = cem;
        cem.flexibilityRegistration(helper.getRegistration());
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
}
