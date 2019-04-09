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
import nl.technolution.appliance.DeviceControllerApp;
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
    protected void initDevice(BattyConfig configuration) {
        this.id = new DeviceId(configuration.getDeviceId());

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
    protected void initEnvironment(Environment environment, BattyConfig configuration) {

    }
}
