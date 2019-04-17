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
package nl.technolution.fritzy.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TooManyListenersException;

import org.slf4j.Logger;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import nl.technolution.core.Log;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.io.tempsensor.TemperatureSensor;
import nl.technolution.fritzy.io.webrelay.WebRelay;

/**
 * Controls Fritzy hardware
 */
public class FritzyController implements IFritzyController {

    private final Logger log = Log.getLogger();

    private WebRelay webRelay;
    private TemperatureSensor tempSensor;

    public FritzyController() {
        //
    }

    @Override
    public void init(FritzyConfig config) {
        try {
            log.info("Opening connection webrelay: {}:{}", config.getHost(), config.getPort());
            webRelay = new WebRelay(InetAddress.getByName(config.getHost()), config.getPort());

            log.info("Opening connection to temp sensor: {}", config.getSerailPort());
            tempSensor = new TemperatureSensor(config.getSerailPort());
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

    @Override
    public WebRelay getWebRelay() {
        return webRelay;
    }

    @Override
    public TemperatureSensor getTemperatureSensor() {
        return tempSensor;
    }
}
