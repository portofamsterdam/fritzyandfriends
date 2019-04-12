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
package nl.technolution.sunny.app;

import org.slf4j.Logger;

import io.dropwizard.setup.Environment;
import nl.technolution.DeviceId;
import nl.technolution.appliance.DeviceControllerApp;
import nl.technolution.core.Log;

/**
 * Device that controls Fridge.
 */
public final class SunnyApp extends DeviceControllerApp<SunnyConfig> {

    private Logger log = Log.getLogger();

    private DeviceId id = null;


    @Override
    public DeviceId getDeviceId() {
        return id;
    }

    @Override
    protected void initDevice(SunnyConfig conf) {
        // TODO MKE: init sunny
    }

    /**
     * Run Fritzy
     * 
     * @param args passed by CLI
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new SunnyApp().run(args);
    }

    @Override
    protected void initEnvironment(Environment environment, SunnyConfig conf) {
        this.id = new DeviceId(conf.getDeviceId());
    }
}
