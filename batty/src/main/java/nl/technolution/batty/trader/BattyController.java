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

import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Uninterruptibles;

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

    void init() {
        log.debug("init batty controller");
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOff();
    }

    /**
     * @param chargeRate Wh charge.
     */
    void charge(double chargeRate) {
        Preconditions.checkArgument(chargeRate >= 0d && chargeRate <= 100d);
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOn();
        Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
        connection.charge((int)chargeRate);
        log.debug("charging at {}%", chargeRate);
    }

    /**
     * @param dischargeRate Wh charge.
     */
    void discharge(double dischargeRate) {
        Preconditions.checkArgument(dischargeRate >= 0d && dischargeRate <= 100d);
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOn();
        Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
        connection.discharge((int)dischargeRate);
        log.debug("discharging at {}%", dischargeRate);
    }

    void stop() {
        log.debug("stopping batty");
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        connection.powerOff();
        log.debug("stopped batty");
    }
}
