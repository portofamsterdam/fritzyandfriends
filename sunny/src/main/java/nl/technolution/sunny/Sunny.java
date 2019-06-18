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

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map.Entry;

import nl.technolution.DeviceId;
import nl.technolution.dropwizard.services.Services;
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
import nl.technolution.protocols.efi.util.Efi;
import nl.technolution.sunny.pvcast.cache.IPvForecastsCacher;
import nl.technolution.sunny.pvcast.model.Forecast;
import nl.technolution.sunny.pvcast.model.Forecasts;

/**
 * Manages Sunny
 */
public class Sunny {

    private final DeviceId deviceId;

    public Sunny(DeviceId devieId) {
        this.deviceId = devieId;
    }

    private double getCurrentUsage() {
        // TODO MKE read actual value in watt
        return 1000d;
    }

    /**
     * @return EFI registration message for Sunny
     */
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

    /**
     * @return EFI update message for Sunny based on PVCast forecasts
     */
    public FlexibilityUpdate getFlexibility() {
        InflexibleForecast update = Efi.build(InflexibleForecast.class, deviceId);
        // NOTE: pvcast provides no probability info so ElectricityProfile is used instead of
        // electricityProbabilityProfile
        ElectricityProfile profile = new ElectricityProfile();

        Forecasts forecasts = Services.get(IPvForecastsCacher.class).getPvForecasts();

        Iterator<Entry<Long, Forecast>> entries = forecasts.getForecasts().entrySet().iterator();

        Entry<Long, Forecast> entry = entries.next();
        // Set validFrom to the time stamp of first entry received from pvcast
        update.setValidFrom(Efi.calendarOfInstant(Instant.ofEpochSecond(entry.getKey())));
        while (entries.hasNext()) {
            Instant entryStart = Instant.ofEpochSecond(entry.getKey());
            Element element = new Element();
            element.setPower(entry.getValue().getPower()); // Watt
            // Move to next item and use its time stamp to calculate duration for current entry. This way the last entry
            // is skipped which is OK as no duration can be calculated for it.
            entry = entries.next();
            Duration duration = Duration.between(entryStart, Instant.ofEpochSecond(entry.getKey()));
            element.setDuration(Efi.DATATYPE_FACTORY.newDuration(duration.toMillis()));
            profile.getElement().add(element);
        }
        update.getForecastProfiles().setElectricityProfile(profile);
        return update;
    }

    /**
     * @return EFI measurement message for Sunny based on actual production.
     */
    public Measurement getMeasurement() {
        Measurement measurement = Efi.build(Measurement.class, deviceId);
        measurement.setMeasurementTimestamp(Efi.calendarOfInstant(Instant.now()));
        ElectricityMeasurement value = new ElectricityMeasurement();
        value.setPower(getCurrentUsage());
        measurement.setElectricityMeasurement(value);
        return measurement;
    }
}