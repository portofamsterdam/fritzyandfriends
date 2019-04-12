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
package nl.technolution.batty.app;

import org.slf4j.Logger;

import io.dropwizard.setup.Environment;
import nl.technolution.DeviceId;
import nl.technolution.Services;
import nl.technolution.appliance.DeviceControllerApp;
import nl.technolution.batty.xstorage.IXStorageConnection;
import nl.technolution.batty.xstorage.XStorageConnection;
import nl.technolution.core.Log;

/**
 * Device that controls Fridge.
 */
public final class BattyApp extends DeviceControllerApp<BattyConfig> {

    private Logger log = Log.getLogger();

    private DeviceId id = null;


    @Override
    public DeviceId getDeviceId() {
        return id;
    }

    @Override
    protected void initDevice(BattyConfig conf) {
        IXStorageConnection c = new XStorageConnection(conf.getHost(), conf.getUsername(), conf.getPassword());
        Services.put(IXStorageConnection.class, c);
        log.info("Power on Battery");
        c.powerOn();
    }

    /**
     * Run Fritzy
     * 
     * @param args passed by CLI
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new BattyApp().run(args);
    }

    @Override
    protected void initEnvironment(Environment environment, BattyConfig conf) {
        this.id = new DeviceId(conf.getDeviceId());
    }
}
