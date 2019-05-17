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
package nl.technolution.batty.efi;

import java.time.Instant;

import nl.technolution.DeviceId;
import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.protocols.efi.Actuator;
import nl.technolution.protocols.efi.Actuators;
import nl.technolution.protocols.efi.CommodityEnum;
import nl.technolution.protocols.efi.DeviceClass;
import nl.technolution.protocols.efi.DeviceDescription;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.util.Efi;

/**
 * Manages Sunny
 */
public class BattyResourceHelper {

    private static final int ACTUATOR_ID = 1;
    private final DeviceId deviceId;

    public BattyResourceHelper(DeviceId devieId) {
        this.deviceId = devieId;
    }

    StorageRegistration getRegistration() {
        StorageRegistration reg = Efi.build(StorageRegistration.class, deviceId);

        DeviceDescription description = new DeviceDescription();
        description.setDeviceClass(DeviceClass.STATIONARY_BATTERY);
        reg.setDeviceDescription(description);

        reg.setFillLevelLabel("Battery charge");
        reg.setFillLevelUnit("kWh");
        
        Actuators actuators = new Actuators();
        Actuator actuator = new Actuator();
        actuator.setId(ACTUATOR_ID);
        actuator.getSupportedCommodity().add(CommodityEnum.ELECTRICITY);
        actuators.getActuator().add(actuator);
        reg.setActuators(actuators);

        reg.setInstructionProcessingDelay(Efi.DATATYPE_FACTORY.newDuration(50));
        return reg;
    }

    StorageStatus getFlexibilityUpdate() {
        StorageStatus update = Efi.build(StorageStatus.class, deviceId);
        update.setValidFrom(Efi.calendarOfInstant(Instant.now()));
        update.setCurrentFillLevel(getMachineData().getSoc());
        return update;
    }

    private MachineData getMachineData() {
        return Services.get(IMachineDataCacher.class).getMachineData();
    }
}