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
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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

    private List<ActuatorInstruction> actuatorInstructions = Lists.newArrayList();
    private ActuatorInstruction activeInstruction;

    public BattyResourceManager(DeviceId deviceId) {
        this.deviceId = deviceId;
        this.helper = new BattyResourceHelper(deviceId);
        this.controller = new BattyController(5000d, 5000d); // TODO MKE configurable max chargerate
    }

    @Override
    public void instruct(Instruction instruction) {
        Preconditions.checkArgument(instruction instanceof StorageInstruction, "Expected storage instruction");
        StorageInstruction storageInstruction = StorageInstruction.class.cast(instruction);
        storageInstruction.getActuatorInstructions().getActuatorInstruction().forEach(actuatorInstructions::add);
    }

    @Override
    public void instructionRevoke(InstructionRevoke instructionRevoke) {
        controller.stop();
    }

    private void handleActuatorInstruction(ActuatorInstruction instruction) {
        if (instruction == null) {
            controller.stop();
        }

        if (instruction.getStartTime().toGregorianCalendar().toInstant().plusSeconds(900).isAfter(Instant.now())) {
            activeInstruction = null;
            controller.stop();
        }

        // NOTE assume one actuatorId exists
        EBattyInstruction battyInstruction = EBattyInstruction.fromRunningModeId(instruction.getRunningModeId());
        switch (battyInstruction) {
        case IDLE:
            controller.stop();
            break;
        case CHARGE:
            controller.charge(1000d); // TODO MKE dynamic charge rate
            break;
        case DISCHARGE:
            controller.discharge(1000d); // TODO MKE dynamic discharge rate
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

        // Set next active instruction
        Iterator<ActuatorInstruction> itr = actuatorInstructions.iterator();
        while (itr.hasNext()) {
            ActuatorInstruction instruction = itr.next();
            if (instruction.getStartTime().toGregorianCalendar().toInstant().isAfter(Instant.now())) {
                activeInstruction = instruction;
                itr.remove();
            }
        }

        handleActuatorInstruction(activeInstruction);

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
