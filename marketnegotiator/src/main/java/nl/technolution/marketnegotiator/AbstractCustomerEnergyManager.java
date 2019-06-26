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
package nl.technolution.marketnegotiator;

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.InstructionStatusUpdate;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.util.Efi;
import nl.technolution.protocols.efi.util.ICustomerEnergyManager;

/**
 * @param <T> FlexibilityRegistration
 * @param <S> FlexibilityUpdate
 */
public abstract class AbstractCustomerEnergyManager<T extends FlexibilityRegistration, S extends FlexibilityUpdate>
        implements ICustomerEnergyManager<T, S> {

    private T registration;
    private DeviceId deviceId;

    @Override
    public final void flexibilityRegistration(T flexibilityRegistration) {
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
        // TODO WHO: implement this or remove here and force subclass to implement it
        System.out.println("TODO: Received measurement: " + measurement.getElectricityMeasurement().getPower());
    }

    public T getRegistration() {
        return registration;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }
}