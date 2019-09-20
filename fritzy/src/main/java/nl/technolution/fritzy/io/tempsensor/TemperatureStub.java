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

import nl.technolution.fritzy.io.webrelay.IWebRelay;

/**
 * Increases or decreases temperature based on relay state
 */
public class TemperatureStub implements ITemperatureSensor {

    private IWebRelay webRelay;
    private long lastCall = System.currentTimeMillis();
    private double temperature = 4;

    /**
     * Constructor
     * 
     * @param webRelay
     */
    public TemperatureStub(IWebRelay webRelay) {
        this.webRelay = webRelay;
    }

    @Override
    public double getTemparature() {
        long now = System.currentTimeMillis();
        long msPassed = now - lastCall;
        try {
            if (webRelay.getState().isRelaystate()) {
                // cooling: temperature decreases 0.1 degree every minute (6 degree / hour)
                temperature -= msPassed / 1000d / 60d * 0.1;
            } else {
                // off: temperature increases 1 degree every hour
                temperature += msPassed / 1000d / 60d / 60d * 1;
            }
            lastCall = now;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return temperature;

    }
}
