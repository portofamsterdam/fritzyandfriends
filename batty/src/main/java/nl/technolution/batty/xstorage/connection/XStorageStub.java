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

import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineInfo;
import nl.technolution.batty.xstorage.types.MeterInfo;
import nl.technolution.batty.xstorage.types.Mode;

/**
 * 
 */
class XStorageStub implements IXStorageConnection {

    int stateOfCharge = 50;

    @Override
    public MachineInfo getMachineInfo() throws XStorageException {
        return MachineInfo.build("StubbedFW", "00:00:00:00:00:00",
                "Stubbed serial", "StubInv", "3", "StubbedFw", "1", "90");
    }

    @Override
    public MachineData getMachineData() throws XStorageException {
        return MachineData.build(Instant.now().toString(), "1234", 35.3d, 230d, 90d, 90.0d, 15.3d, stateOfCharge, 1, 1,
                1d, 1d,
                0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0, Sets.newHashSet(), Sets.newHashSet(), 1,
                Mode.getModeByInt(0), 1d, 0.0d);
    }

    @Override
    public BmsData getBmsData() throws XStorageException {
        return BmsData.build(1.5d, 1.3d, 90, 40, 0);
    }

    @Override
    public void charge(int percentage) throws XStorageException {
        if (stateOfCharge < percentage) {
            this.stateOfCharge = percentage;
        }
    }

    @Override
    public void discharge(int percentage) throws XStorageException {
        if (stateOfCharge > percentage) {
            this.stateOfCharge = percentage;
        }
    }

    @Override
    public void powerOn() throws XStorageException {
        // Nothing to stub
    }

    @Override
    public void powerOff() throws XStorageException {
        // Nothing to stub
    }

    @Override
    public MeterInfo getMeterInfo() throws XStorageException {
        return MeterInfo.build(90.0d, 0.0d, 1, 5, 20, 1.0d, 50.0d, 0, 0, 0);
    }

}
