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

import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.InflexibleForecast;
import nl.technolution.protocols.efi.InflexibleRegistration;
import nl.technolution.protocols.efi.InflexibleUpdate;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class SunnyNegotiator extends AbstractCustomerEnergyManager<InflexibleRegistration, InflexibleUpdate> {

    private final MarketConfig config;
    private final SunnyResourceManager resourceManager;

    InflexibleForecast forecast;

    /**
     *
     * @param config config used for trading
     * @param resourceManager to control devices
     */
    public SunnyNegotiator(MarketConfig config, SunnyResourceManager resourceManager) {
        this.config = config;
        this.resourceManager = resourceManager;
    }

    /**
     * Call periodicly to evaluate market changes
     */
    public void evaluate() {

        // Sample charge instruction
        // TODO WHO: implement this
        // StorageInstruction instruction = Efi.build(StorageInstruction.class, getDeviceId());
        // ActuatorInstructions actInstuctions = new ActuatorInstructions();
        // ActuatorInstruction actInstruction = new ActuatorInstruction();
        // actInstruction.setActuatorId(BattyResourceHelper.ACTUATOR_ID);
        // actInstruction.setRunningModeId(EBattyInstruction.CHARGE.getRunningModeId());
        // actInstruction.setStartTime(Efi.calendarOfInstant(Efi.getNextQuarter()));
        // actInstuctions.getActuatorInstruction().add(actInstruction);
        // instruction.setActuatorInstructions(actInstuctions);
        //
        // resourceManager.instruct(instruction);
        // TODO MKE trade with market and apply changes to device
    }

    @Override
    public Instruction flexibilityUpdate(InflexibleUpdate update) {
        if (update instanceof InflexibleForecast) {
            forecast = (InflexibleForecast)update;
        }
        // TODO WHO: returning this empty shell seems not very useful and it is never used?? ask Martin...
        return Efi.build(StorageInstruction.class, getDeviceId());
    }
}
