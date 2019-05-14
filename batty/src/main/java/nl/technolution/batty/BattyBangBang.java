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
package nl.technolution.batty;

import nl.technolution.DeviceId;
import nl.technolution.batty.app.BattyConfig;
import nl.technolution.marketnegotiator.storage.BatteryNegotiator;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.util.ICustomerEnergyManager;

/**
 * 
 */
public class BattyBangBang implements IBattyTrader {

    private BattyResourceManager resourceManager;
    private ICustomerEnergyManager<StorageRegistration, StorageStatus> cem;

    @Override
    public void init(BattyConfig config) {
        resourceManager = new BattyResourceManager(new DeviceId(config.getDeviceId()));
        cem = new BatteryNegotiator(config.getMarket(), resourceManager);
    }

}
