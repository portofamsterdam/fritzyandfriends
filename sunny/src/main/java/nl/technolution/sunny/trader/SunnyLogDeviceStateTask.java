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
package nl.technolution.sunny.trader;

import java.util.concurrent.TimeUnit;

import com.ghgande.j2mod.modbus.ModbusException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.ITaskRunner;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.EventLogger;
import nl.technolution.sunny.solaredge.ISESessionFactory;

/**
 * 
 */
@TimedTask(period = 30, unit = TimeUnit.SECONDS)
public class SunnyLogDeviceStateTask implements ITaskRunner {
    private static final Logger LOG = Log.getLogger();

    @Override
    public void execute() {
        logDeviceState();
    }

    @SuppressWarnings("unchecked")
    private static final void logDeviceState() {
        String inverterPowerString = "?";

        try {
            double inverterPower = Services.get(ISESessionFactory.class).getSESession().getInverterPower();
            inverterPowerString = String.format("%.2f", inverterPower);
        } catch (ModbusException e) {
            LOG.warn("Unable to retrive generation power", e);
        }

        EventLogger logger = new EventLogger(Services.get(IFritzyApiFactory.class).build());
        logger.logDeviceState(new ImmutablePair<String, Object>("production", inverterPowerString));
    }
}
