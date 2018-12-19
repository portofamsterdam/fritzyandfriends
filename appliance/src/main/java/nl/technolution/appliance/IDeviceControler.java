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
package nl.technolution.appliance;

import nl.technolution.protocols.efi.util.IEnergyApp;

/**
 * 
 */
public interface IDeviceControler {

    /**
     * Is used by a device to identify itself
     * 
     * @return id of device
     */
    DeviceId getDeviceId();
    
    /**
     * Initialize hardware
     * 
     * @return true if oke
     */
    boolean init();

    /**
     * Register an appliance to this energy app
     * 
     * @param energyApp to connect to
     */
    void registerTo(IEnergyApp energyApp);

}
