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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TooManyListenersException;

import org.slf4j.Logger;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import nl.technolution.Services;
import nl.technolution.appliance.DeviceControllerApp;
import nl.technolution.appliance.DeviceId;
import nl.technolution.core.Log;
import nl.technolution.fritzy.tempsensor.TemperatureSensor;
import nl.technolution.fritzy.webrelay.WebRelay;

/**
 * Device that controls Fridge.
 */
public final class Fritzy extends DeviceControllerApp<FritzyConfig> implements IFritzy {

    private Logger log = Log.getLogger();

    private DeviceId id = null;

    private WebRelay webRelay;
    private TemperatureSensor tempSensor;

    @Override
    public DeviceId getDeviceId() {
        return id;
    }

    @Override
    public WebRelay getWebRelay() {
        return webRelay;
    }

    @Override
    public TemperatureSensor getTemperatureSensor() {
        return tempSensor;
    }

    @Override
    protected void initDevice(FritzyConfig configuration) {
        this.id = new DeviceId(configuration.getDevicveId());
        Services.put(IFritzy.class, this);
        try {
            log.info("Opening connection webrelay: {}:{}", configuration.getHost(), configuration.getPort());
            webRelay = new WebRelay(InetAddress.getByName(configuration.getHost()), configuration.getPort());

            log.info("Opening connection to temp sensor: {}", configuration.getSerailPort());
            tempSensor = new TemperatureSensor(configuration.getSerailPort());
            tempSensor.init();
        } catch (UnknownHostException e) {
            log.error("Failed to init WebRelay: {}", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Unable to read fritzy config: {}", e);
            throw new RuntimeException(e);
        } catch (NoSuchPortException | PortInUseException | TooManyListenersException | 
                UnsupportedCommOperationException e) {
            log.error("Unable init comm port: {}", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Run Fritzy
     * 
     * @param args passed by CLI
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new Fritzy().run(args);
    }
}
