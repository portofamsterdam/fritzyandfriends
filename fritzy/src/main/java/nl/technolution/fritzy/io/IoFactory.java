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

import com.google.common.base.Preconditions;

import org.slf4j.Logger;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import nl.technolution.Log;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.io.tempsensor.ITemperatureSensor;
import nl.technolution.fritzy.io.tempsensor.TemperatureStub;
import nl.technolution.fritzy.io.tempsensor.TemperatureSensor;
import nl.technolution.fritzy.io.webrelay.IWebRelay;
import nl.technolution.fritzy.io.webrelay.RelayStub;
import nl.technolution.fritzy.io.webrelay.WebRelay;

/**
 * 
 */
public class IoFactory implements IIoFactory {

    private static final Logger LOG = Log.getLogger();

    private ITemperatureSensor tempSensor;
    private IWebRelay webRelay;
    
    @Override
    public void init(FritzyConfig config) {
        tempSensor = config.isStubTemparature() ? new TemperatureStub() : getSerialSensor(config.getSerialPort());
        webRelay = config.isStubRelay() ? new RelayStub() : getWebRelay(config.getHost(), config.getPort());
    }

    @Override
    public ITemperatureSensor getTemparatureSensor() {
        Preconditions.checkNotNull(tempSensor, "IoFactory not initialised");
        return tempSensor;
    }

    @Override
    public IWebRelay getWebRelay() {
        Preconditions.checkNotNull(webRelay, "IoFactory not initialised");
        return webRelay;
    }

    private static IWebRelay getWebRelay(String host, int port) {
        LOG.info("Opening connection webrelay: {}:{}", host, port);
        try {
            return new WebRelay(InetAddress.getByName(host), port);
        } catch (UnknownHostException e) {
            throw new IllegalStateException("unable to reach webrelay", e);
        }
    }

    private static ITemperatureSensor getSerialSensor(String serialPort) {
        LOG.info("Opening connection to temp sensor: {}", serialPort);
        TemperatureSensor tempSensor = new TemperatureSensor(serialPort);
        try {
            tempSensor.init();
        } catch (NoSuchPortException | PortInUseException | IOException | TooManyListenersException | 
                UnsupportedCommOperationException e) {
            throw new IllegalStateException("Unable to reach temparature sensor", e);
        }
        return tempSensor;
    }
}
