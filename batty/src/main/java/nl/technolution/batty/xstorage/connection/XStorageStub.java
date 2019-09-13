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
package nl.technolution.batty.xstorage.connection;

import com.google.common.collect.Sets;

import org.joda.time.Instant;
import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineInfo;
import nl.technolution.batty.xstorage.types.MeterInfo;
import nl.technolution.batty.xstorage.types.Mode;

/**
 * 
 */
public final class XStorageStub implements IXStorageConnection {

    private static final Logger LOG = Log.getLogger();
    private static XStorageStub storageStub;


    private int stateOfCharge;
    private boolean charging;
    private boolean discharging;

    private XStorageStub() {
        // hide
    }

    /**
     * Singleton instance of stub
     * 
     * @return
     */
    public static XStorageStub instance() {
        synchronized (XStorageStub.class) {
            if (storageStub == null) {
                LOG.info("Create first instance of XStorageStub");
                storageStub = new XStorageStub();
                XStorageStub.reset();
            }
        }
        return storageStub;
    }

    /**
     * 
     */
    public static void reset() {
        synchronized (XStorageStub.class) {
            LOG.info("Reset instance of XStorageStub");
            storageStub.stateOfCharge = 50;
            storageStub.charging = false;
            storageStub.discharging = false;
        }
    }

    @Override
    public MachineInfo getMachineInfo() throws XStorageException {
        LOG.info("getMachineInfo");
        return MachineInfo.build("StubbedFW", "00:00:00:00:00:00",
                "Stubbed serial", "StubInv", "3", "StubbedFw", "1", "90");
    }

    @Override
    public MachineData getMachineData() throws XStorageException {
        LOG.info("getMachineData");
        return MachineData.build(Instant.now().toString(), "1234", 35.3d, 230d, 90d, 90.0d, 15.3d, stateOfCharge, 1, 1,
                1d, 1d,
                0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0, Sets.newHashSet(), Sets.newHashSet(), 1,
                Mode.getModeByInt(0), 1d, 0.0d);
    }

    @Override
    public BmsData getBmsData() throws XStorageException {
        LOG.info("getBmsData");
        return BmsData.build(1.5d, 1.3d, 90, 40, 0);
    }

    @Override
    public void charge(int percentage) throws XStorageException {
        charging = true;
        discharging = false;

        if (stateOfCharge <= 90) {
            stateOfCharge += 10;
        }
        LOG.info("charging {}, discharging {} state {}", charging, discharging, stateOfCharge);
    }

    @Override
    public void discharge(int percentage) throws XStorageException {
        charging = false;
        discharging = true;

        if (stateOfCharge >= 10) {
            stateOfCharge -= 10;
        }
        LOG.info("charging {}, discharging {} state {}", charging, discharging, stateOfCharge);
    }

    @Override
    public void powerOn() throws XStorageException {
        LOG.info("powerOn");
        charging = false;
        discharging = false;
    }

    @Override
    public void powerOff() throws XStorageException {
        LOG.info("powerOff");
        charging = false;
        discharging = false;
    }

    @Override
    public MeterInfo getMeterInfo() throws XStorageException {
        LOG.info("getMeterInfo");
        return MeterInfo.build(90.0d, 0.0d, 1, 5, 20, 1.0d, 50.0d, 0, 0, 0);
    }

    public int getStateOfCharge() {
        return stateOfCharge;
    }

    /**
     * @param stateOfCharge to set
     */
    public void setStateOfCharge(int stateOfCharge) {
        LOG.info("setStateOfCharge {}", stateOfCharge);
        this.stateOfCharge = stateOfCharge;
    }

    public boolean isCharging() {
        LOG.info("charging {}, discharging {} state {}", charging, discharging, stateOfCharge);
        return charging;
    }

    /**
     * @param charging to set
     */
    public void setCharging(boolean charging) {
        LOG.info("setCharging {}", charging);
        this.charging = charging;
    }

    public boolean isDischarging() {
        LOG.info("charging {}, discharging {} state {}", charging, discharging, stateOfCharge);
        return discharging;
    }

    /**
     * @param discharging to set
     */
    public void setDischarging(boolean discharging) {
        LOG.info("setDischarging {}", discharging);
        this.discharging = discharging;
    }
}
