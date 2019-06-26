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
package nl.technolution.sunny.trader;

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.InflexibleRegistration;
import nl.technolution.protocols.efi.InflexibleUpdate;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionRevoke;
import nl.technolution.protocols.efi.util.ICustomerEnergyManager;
import nl.technolution.protocols.efi.util.IResourceManager;

/**
 * Resource Manager of sunny
 */
public class SunnyResourceManager implements IResourceManager {
    private final SunnyResourceHelper helper;

    private ICustomerEnergyManager<InflexibleRegistration, InflexibleUpdate> cem;

    public SunnyResourceManager(DeviceId deviceId) {
        this.helper = new SunnyResourceHelper(deviceId);
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
    public void registerCustomerEnergyManager(SunnyNegotiator cem) {
        this.cem = cem;
        cem.flexibilityRegistration(helper.getRegistration());
        cem.flexibilityUpdate(helper.getFlexibilityUpdate());
    }

    /**
     * Send measurement
     */
    public void sendMeasurement() {
        cem.measurement(helper.getMeasurement());
    }

    @Override
    public void instruct(Instruction instruction) {
        throw new Error("Curtailment not supported thus no instructions are expected");
    }

    @Override
    public void instructionRevoke(InstructionRevoke instructionRevoke) {
        throw new Error("Curtailment not supported thus no instruction renove is expected");
    }
}
