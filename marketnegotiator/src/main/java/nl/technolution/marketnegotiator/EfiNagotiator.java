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

import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionStatusUpdate;
import nl.technolution.protocols.efi.Measurement;

/**
 * 
 */
public class EfiNagotiator implements ICustomerEnergyManager {

    public EfiNagotiator() {
        // 
    }

    @Override
    public void flexibilityRegistration(FlexibilityRegistration flexibilityRegistration) {
        // 

    }

    @Override
    public Instruction flexibilityUpdate(FlexibilityUpdate update) {
        // 
        return null;
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
        // 

    }

}
