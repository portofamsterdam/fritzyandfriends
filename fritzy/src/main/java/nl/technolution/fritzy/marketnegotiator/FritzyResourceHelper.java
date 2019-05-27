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
package nl.technolution.fritzy.marketnegotiator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.CommodityEnum;
import nl.technolution.protocols.efi.DeviceClass;
import nl.technolution.protocols.efi.DeviceDescription;
import nl.technolution.protocols.efi.SequentialProfile;
import nl.technolution.protocols.efi.SequentialProfiles;
import nl.technolution.protocols.efi.ShiftableRegistration;
import nl.technolution.protocols.efi.ShiftableUpdate;
import nl.technolution.protocols.efi.SupportedCommodities;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class FritzyResourceHelper {

    private DeviceId deviceId;

    /**
     * Constructor for {@link FritzyResourceHelper} objects
     * 
     * @param deviceId
     *
     */
    public FritzyResourceHelper(DeviceId deviceId) {
        this.deviceId = deviceId;
    }

    ShiftableRegistration getRegistration() {
        ShiftableRegistration reg = Efi.build(ShiftableRegistration.class, deviceId);
        SupportedCommodities commodity = new SupportedCommodities();
        commodity.getCommodityType().add(CommodityEnum.ELECTRICITY);
        DeviceDescription description = new DeviceDescription();
        description.setDeviceClass(DeviceClass.REFRIGERATOR);
        reg.setDeviceDescription(description);
        reg.setSupportedCommodities(commodity);
        reg.setInstructionProcessingDelay(Efi.DATATYPE_FACTORY.newDuration(0));
        return reg;
    }

    ShiftableUpdate getFlexibilityUpdate() {

        ShiftableUpdate shiftableUpdate = Efi.build(ShiftableUpdate.class, deviceId);
        shiftableUpdate.setEndBefore(Efi.calendarOfInstant(Instant.now().plus(1, ChronoUnit.DAYS)));
        SequentialProfiles profiles = new SequentialProfiles();
        List<SequentialProfile> sequentialProfileList = profiles.getSequentialProfile();

        // TODO MKE create flexibility
        SequentialProfile profile = new SequentialProfile();

        sequentialProfileList.add(profile);
        shiftableUpdate.setSequentialProfiles(profiles);
        return shiftableUpdate;
    }

}
