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
package nl.technolution.marketnegotiator.shiftable;

import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionStatusUpdate;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.ShiftableRegistration;
import nl.technolution.protocols.efi.ShiftableUpdate;

/**
 * 
 */
public class ShiftableNegotiator extends AbstractCustomerEnergyManager<ShiftableRegistration, ShiftableUpdate> {

    private Double temparature;

    @Override
    public Instruction flexibilityUpdate(ShiftableUpdate update) {
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

    }

}
