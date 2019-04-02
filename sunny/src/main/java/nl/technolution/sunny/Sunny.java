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
package nl.technolution.sunny;

import java.time.Instant;

import nl.technolution.appliance.resourcemanager.IDeviceResourceHandler;
import nl.technolution.protocols.efi.CommodityEnum;
import nl.technolution.protocols.efi.DeviceClass;
import nl.technolution.protocols.efi.DeviceDescription;
import nl.technolution.protocols.efi.ElectricityProfile;
import nl.technolution.protocols.efi.ElectricityProfile.Element;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.InflexibleForecast;
import nl.technolution.protocols.efi.InflexibleRegistration;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.Measurement.ElectricityMeasurement;
import nl.technolution.protocols.efi.SupportedCommodities;
import nl.technolution.protocols.efi.util.DeviceId;
import nl.technolution.protocols.efi.util.Efi;

/**
 * Manages Sunny
 */
public class Sunny implements IDeviceResourceHandler {

    private final DeviceId deviceId;

    public Sunny(DeviceId devieId) {
        this.deviceId = devieId;
    }

    private double getCurrentUsage() {
        // TODO MKE read actual value in watt
        return 1000d;
    }

    @Override
    public FlexibilityRegistration getRegistration() {
        InflexibleRegistration reg = Efi.build(InflexibleRegistration.class, deviceId);
        SupportedCommodities commodity = new SupportedCommodities();

        commodity.getCommodityType().add(CommodityEnum.ELECTRICITY);
        DeviceDescription description = new DeviceDescription();
        description.setDeviceClass(DeviceClass.PV_PANEL);
        reg.setSupportedCommodities(commodity);
        reg.setDeviceDescription(description);
        reg.setInstructionProcessingDelay(Efi.DATATYPE_FACTORY.newDuration(0));
        return reg;
    }

    @Override
    public FlexibilityUpdate getFlexibility() {
        InflexibleForecast update = Efi.build(InflexibleForecast.class, deviceId);
        ElectricityProfile profile = new ElectricityProfile();
        // TODO MKE create actual expectation based on weather
        Element element = new Element();
        element.setPower(1000d); // watt
        element.setDuration(Efi.DATATYPE_FACTORY.newDuration(900000));
        profile.getElement().add(element);
        // TODO MKE build propability
        update.getForecastProfiles().setElectricityProfile(profile);
        return update;
    }

    @Override
    public Measurement getMeasurement() {
        Measurement measurement = Efi.build(Measurement.class, deviceId);
        measurement.setMeasurementTimestamp(Efi.calendarOfInstant(Instant.now()));
        ElectricityMeasurement value = new ElectricityMeasurement();
        value.setPower(getCurrentUsage());
        measurement.setElectricityMeasurement(value);
        return measurement;
    }

    @Override
    public DeviceId getDeviceId() {
        return deviceId;
    }
}