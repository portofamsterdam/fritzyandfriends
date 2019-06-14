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

import nl.technolution.market.MarketConfig;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.ActuatorInstruction;
import nl.technolution.protocols.efi.ActuatorInstructions;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.StorageUpdate;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class BatteryNegotiator extends AbstractCustomerEnergyManager<StorageRegistration, StorageUpdate> {

    private final MarketConfig config;
    private final BattyResourceManager resourceManager;

    private Double fillLevel;

    /**
     *
     * @param config config used for trading
     * @param resourceManager to control devices
     */
    public BatteryNegotiator(MarketConfig config, BattyResourceManager resourceManager) {
        this.config = config;
        this.resourceManager = resourceManager;
    }

    @Override
    public Instruction flexibilityUpdate(StorageUpdate storageStatus) {
        if (storageStatus instanceof StorageStatus) {
            fillLevel = ((StorageStatus)storageStatus).getCurrentFillLevel();
        }
        return Efi.build(StorageInstruction.class, getDeviceId());

    }

    /**
     * Call periodicly to evaluate market changes
     */
    public void evaluate() {


        // Sample charge instruction
        StorageInstruction instruction = Efi.build(StorageInstruction.class, getDeviceId());
        ActuatorInstructions actInstuctions = new ActuatorInstructions();
        ActuatorInstruction actInstruction = new ActuatorInstruction();
        actInstruction.setActuatorId(BattyResourceHelper.ACTUATOR_ID);
        actInstruction.setRunningModeId(EBattyInstruction.CHARGE.getRunningModeId());
        actInstruction.setStartTime(Efi.calendarOfInstant(Efi.getNextQuarter()));
        actInstuctions.getActuatorInstruction().add(actInstruction);
        instruction.setActuatorInstructions(actInstuctions);

        resourceManager.instruct(instruction);
        // TODO MKE trade with market and apply changes to device
    }



    public Double getFillLevel() {
        return fillLevel;
    }
}
