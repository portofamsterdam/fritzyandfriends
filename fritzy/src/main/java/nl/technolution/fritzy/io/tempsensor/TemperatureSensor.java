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
package nl.technolution.fritzy.io.tempsensor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * Temperature sensor of Fritzy
 */
public class TemperatureSensor implements ITemperatureSensor {

    private static final int BAUD = 115200;

    private final String serialPort;

    private SerialPort commPort;
    private InputStream in;

    private volatile double temparature = 0.0d;

    public TemperatureSensor(String serialPort) {
        this.serialPort = serialPort;
    }

    /**
     * Init the commport connection to read the temparature sensor
     * 
     * @throws NoSuchPortException
     * @throws PortInUseException
     * @throws IOException
     * @throws TooManyListenersException
     * @throws UnsupportedCommOperationException
     * 
     * @throws Exception when connection cannot be made
     */
    public void init() throws NoSuchPortException, PortInUseException, IOException, TooManyListenersException,
            UnsupportedCommOperationException {

        String path = System.getProperty("java.library.path");
        System.out.println(path);

        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPort);
        commPort = (SerialPort)portIdentifier.open("Temparature", 2000);
        in = commPort.getInputStream();

        commPort.setSerialPortParams(BAUD, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        commPort.addEventListener(new SerialPortEventListener() {

            @Override
            public void serialEvent(SerialPortEvent event) {

                if (SerialPortEvent.DATA_AVAILABLE == event.getEventType()) {
                    try {
                        byte[] buffer = new byte[20];
                        while (in.available() > 0) {
                            in.read(buffer);
                        }
                        String stringValue = new String(buffer, StandardCharsets.UTF_8);
                        temparature = Double.parseDouble(stringValue.trim());
                    } catch (IOException e) {
                        temparature = Double.MIN_VALUE;
                    }
                }

            }
        });
        commPort.notifyOnDataAvailable(true);
    }

    /**
     * try to close the connection
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        if (commPort != null) {
            commPort.close();
        }
        if (in != null) {
            in.close();
        }
    }

    @Override
    public double getTemparature() {
        return temparature;
    }
}
