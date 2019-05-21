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

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionStatusUpdate;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.util.ICustomerEnergyManager;

/**
 * 
 */
public class FritzyFlexibilityManager implements ICustomerEnergyManager {

    private DeviceId deviceId;

    /**
     *
     */
    public FritzyFlexibilityManager(DeviceId deviceId) {
        this.deviceId = deviceId;
    }


    @Override
    public void flexibilityRegistration(FlexibilityRegistration flexibilityRegistration) {
    }

    @Override
    public Instruction flexibilityUpdate(FlexibilityUpdate update) {
        return null;
    }

    @Override
    public void instructionStatusUpdate(InstructionStatusUpdate instructionStatusUpdate) {
        //

    }

    @Override
    public void measurement(Measurement measurement) {
        //

    }

    @Override
    public void flexibilityRevoke(FlexibilityRevoke revocation) {
        //

    }

}
