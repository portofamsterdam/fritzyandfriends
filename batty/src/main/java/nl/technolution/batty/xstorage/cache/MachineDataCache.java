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
package nl.technolution.batty.xstorage.cache;

import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;

import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.connection.IXStorageConnection;
import nl.technolution.batty.xstorage.connection.IXStorageFactory;
import nl.technolution.batty.xstorage.connection.XStorageException;
import nl.technolution.batty.xstorage.types.EModeByte1;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.EventLogger;

/**
 * 
 */
public class MachineDataCache implements IMachineDataCacher {

    private static final Set<EModeByte1> CHARGE_MODES = EnumSet.of(EModeByte1.CHARGE, EModeByte1.DISCHARGE);
    private MachineData cachedSoc;

    @Override
    public void init(BattyConfig config) {
        // 
    }

    @Override
    public void update() {
        IXStorageConnection connection = Services.get(IXStorageFactory.class).getConnection();
        try {
            cachedSoc = connection.getMachineData();
            logState(cachedSoc);
        } catch (XStorageException ex) {
            throw new IllegalStateException("Unable to retrieve state of charge", ex);
        }
    }

    @Override
    public MachineData getMachineData() {
        if (cachedSoc == null) {
            update();
        }
        return cachedSoc;
    }

    @SuppressWarnings("unchecked")
    private void logState(MachineData machineData) {
        EventLogger logger = new EventLogger(Services.get(IFritzyApiFactory.class).build());
        boolean isCharging = CHARGE_MODES.contains(machineData.getMode().getMode1());
        String batteryState = machineData.getMode().getMode1().name();
        logger.logDeviceState(
                new ImmutablePair<String, Object>("isCharging", isCharging),
                new ImmutablePair<String, Object>("batteryState", batteryState),
                new ImmutablePair<String, Object>("chargeLevel", machineData.getSoc()));
    }
}
