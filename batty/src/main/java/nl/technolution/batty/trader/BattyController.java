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
package nl.technolution.batty.trader;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.batty.xstorage.connection.IXStorageConnection;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.dropwizard.services.Services;

/**
 * 
 */
class BattyController {

    private final Logger log = Log.getLogger();

    private double maxChargeRate;
    private double maxDischargeRate;

    /**
     * Constructor for {@link BattyController} objects
     *
     * @param maxChargeRate
     * @param maxDischargeRate
     */
    BattyController(double maxChargeRate, double maxDischargeRate) {
        this.maxChargeRate = maxChargeRate;
        this.maxDischargeRate = maxDischargeRate;
    }

    void init() {
        log.debug("init batty controller");
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOff();
    }

    /**
     * @param chargeRate Wh charge.
     */
    void charge(double chargeRate) {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOn();
        double chargeRatePercentage = chargeRate / maxChargeRate * 100d;
        log.debug("charging at {} ({}%)", chargeRate, chargeRatePercentage);
        connection.charge((int)chargeRatePercentage);
    }

    /**
     * @param dischargeRate Wh charge.
     */
    void discharge(double dischargeRate) {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOn();
        double dischargeRatePercentage = dischargeRate / maxDischargeRate * 100d;
        log.debug("discharging at {} ({}%)", dischargeRate, dischargeRatePercentage);
        connection.discharge((int)dischargeRatePercentage);
    }

    void stop() {
        log.debug("stopping batty");
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOff();
    }
}
