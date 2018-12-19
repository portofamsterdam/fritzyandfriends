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
package nl.technolution.netty;

import com.google.common.base.Preconditions;

import nl.technolution.appliance.DeviceId;
import nl.technolution.appliance.IDeviceControler;
import nl.technolution.protocols.efi.util.IEnergyApp;

/**
 * Simulator for net Power
 */
public class Netty implements IDeviceControler {

    // TODO MKE
    private final DeviceId deviceId = null;

    private IEnergyApp energyApp;

    @Override
    public DeviceId getDeviceId() {
        return deviceId;
    }
    

    @Override
    public void registerTo(IEnergyApp energyApp) {
        if (energyApp != null) {
            deregister();
        }
        this.energyApp = energyApp;
        register();
    }

    private void register() {
        Preconditions.checkNotNull(energyApp, "Cannot not register, unknown energy app");

    }

    private void deregister() {
        Preconditions.checkNotNull(energyApp, "Cannot not deregister, unknown energy app");
        //

    }

    @Override
    public boolean init() {
        //
        return false;
    }
}
