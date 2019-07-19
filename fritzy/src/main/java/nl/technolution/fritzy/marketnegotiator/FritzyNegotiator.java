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

import nl.technolution.dropwizard.MarketConfig;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageUpdate;

/**
 * 
 */
public class FritzyNegotiator extends AbstractCustomerEnergyManager<StorageRegistration, StorageUpdate> {

    public FritzyNegotiator(MarketConfig market, FritzyResourceManager resourceManager) {
        //
    }

    @Override
    public Instruction flexibilityUpdate(StorageUpdate update) {
        // TODO wilfred.hoogerbrugge Auto-generated method stub
        return null;
    }

    /**
     * 
     */
    public void evaluate() {
        // TODO wilfred.hoogerbrugge Auto-generated method stub
    }
}
