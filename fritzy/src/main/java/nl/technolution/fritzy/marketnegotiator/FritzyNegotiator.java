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

import nl.technolution.market.MarketConfig;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.ShiftableRegistration;
import nl.technolution.protocols.efi.ShiftableUpdate;

/**
 * 
 */
public class FritzyNegotiator extends AbstractCustomerEnergyManager<ShiftableRegistration, ShiftableUpdate> {

    public FritzyNegotiator(MarketConfig market, FritzyResourceManager resourceManager) {
        // 
    }

    @Override
    public Instruction flexibilityUpdate(ShiftableUpdate update) {
        //
        return null;
    }

}
