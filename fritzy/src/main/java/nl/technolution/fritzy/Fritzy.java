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
package nl.technolution.fritzy;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;

import nl.technolution.appliance.DeviceId;
import nl.technolution.appliance.IDeviceControler;
import nl.technolution.core.Log;
import nl.technolution.fritzy.webrelay.WebRelay;
import nl.technolution.protocols.efi.util.IEnergyApp;

/**
 * Device that controls Fridge.
 */
public class Fritzy implements IDeviceControler {

    private Logger log = Log.getLogger();

    private final IEnergyApp energyApp = null;
    private final DeviceId id = null;

    private WebRelay webRelay;

    @Override
    public DeviceId getDeviceId() {
        return id;
    }

    @Override
    public void registerTo(IEnergyApp energyApp) {
        //

    }

    @Override
    public boolean init() {
        try {
            webRelay = new WebRelay(InetAddress.getByName("172.16.192.17"));
        } catch (UnknownHostException e) {
            log.debug("Failed to init WebRelay: {}", e);
            return false;
        }
        return true;
    }

}
