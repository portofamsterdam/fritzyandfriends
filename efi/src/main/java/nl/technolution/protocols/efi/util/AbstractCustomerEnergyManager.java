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
package nl.technolution.protocols.efi.util;

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.InstructionStatusUpdate;
import nl.technolution.protocols.efi.Measurement;

/**
 * @param <T> FlexibilityRegistration
 * @param <S> FlexibilityUpdate
 */
public abstract class AbstractCustomerEnergyManager<T extends FlexibilityRegistration, S extends FlexibilityUpdate>
        implements ICustomerEnergyManager<T, S> {

    private final Logger log = Log.getLogger();

    private T registration;
    private DeviceId deviceId;

    @Override
    public void flexibilityRegistration(T flexibilityRegistration) {
        this.deviceId = Efi.getDeviceId(flexibilityRegistration);
        this.registration = flexibilityRegistration;
    }

    @Override
    public void instructionStatusUpdate(InstructionStatusUpdate instructionStatusUpdate) {
        //
    }

    @Override
    public void flexibilityRevoke(FlexibilityRevoke revocation) {
        //
    }

    @Override
    public void measurement(Measurement measurement) {
        log.debug("Received measurement but nothing is done with it: " +
                measurement.getElectricityMeasurement().getPower());
    }

    public T getRegistration() {
        return registration;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }
}