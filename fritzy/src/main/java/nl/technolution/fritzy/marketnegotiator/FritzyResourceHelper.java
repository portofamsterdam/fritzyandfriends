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

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import nl.technolution.DeviceId;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.io.IIoFactory;
import nl.technolution.protocols.efi.CommodityEnum;
import nl.technolution.protocols.efi.DeviceClass;
import nl.technolution.protocols.efi.DeviceDescription;
import nl.technolution.protocols.efi.ElectricityProfile;
import nl.technolution.protocols.efi.ElectricityProfile.Element;
import nl.technolution.protocols.efi.SequentialProfile;
import nl.technolution.protocols.efi.SequentialProfileAlternative;
import nl.technolution.protocols.efi.SequentialProfiles;
import nl.technolution.protocols.efi.ShiftableRegistration;
import nl.technolution.protocols.efi.ShiftableUpdate;
import nl.technolution.protocols.efi.SupportedCommodities;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class FritzyResourceHelper {

    private FritzyConfig config;
    private DeviceId deviceId;
    private double coolingDegreesPerSecond = 1d / 60 / 60;
    private double heatingDegreesPerSecond = 1d / 10 / 60;

    private class State {
        double temperature;
        boolean isCooling;
        Instant instant;

        State() {
            instant = Instant.now();
        }
    }

    private State previousState;
    private State newState;

    /**
     * Constructor for {@link FritzyResourceHelper} objects
     * 
     * @param config
     *
     */
    public FritzyResourceHelper(FritzyConfig config) {
        this.config = config;
        deviceId = new DeviceId(config.getDeviceId());
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

    private boolean coolingPossible() {
        double midTemp = config.getMaxTemp() - config.getMinTemp() / 2;

        // cooling is possible when:
        // not running + temperature above 'middle temp' (start only above 'middle' temp to prevent continuously on/off)
        // AND temp above min
        return ((!newState.isCooling && (newState.temperature > midTemp)) &&
                (newState.temperature > config.getMinTemp()));
    }

    private boolean coolingNeeded() {
        double midTemp = config.getMaxTemp() - config.getMinTemp() / 2;
        // cooling is needed when:
        // running + temperature still above 'middle temp' (stop only above 'middle' temp to prevent continuously
        // on/off)
        // OR temp above max
        return ((newState.isCooling && (newState.temperature > midTemp)) ||
                (newState.temperature > config.getMaxTemp()));
    }

    private Instant getMayStart() {
        return newState.instant.plus(
                (long)((config.getCoolingAllowedBottemTemp() - newState.temperature) / heatingDegreesPerSecond),
                ChronoUnit.SECONDS);
    }

    private Instant getMustStart() {
        return newState.instant.plus((long)(config.getMaxTemp() - newState.temperature / heatingDegreesPerSecond),
                ChronoUnit.SECONDS);
    }

    private void updateState() {
        // update internal state
        newState = new State();

        IIoFactory fritzy = Services.get(IIoFactory.class);
        try {
            newState.isCooling = fritzy.getWebRelay().getState().isRelaystate();
        } catch (IOException e) {
            newState.isCooling = false;
        }
        newState.temperature = fritzy.getTemparatureSensor().getTemparature();

        // check if state change and recalculate cooling / heating speed
        if (previousState.isCooling != newState.isCooling) {
            if (previousState.isCooling) {
                double newCoolingDegreesPerSecond = (previousState.temperature - newState.temperature) /
                        Duration.between(previousState.instant, newState.instant).getSeconds();
                coolingDegreesPerSecond = (coolingDegreesPerSecond + newCoolingDegreesPerSecond) / 2;
            } else {
                double newHeatingDegreesPerSecond = (newState.temperature - previousState.temperature) /
                        Duration.between(previousState.instant, newState.instant).getSeconds();
                heatingDegreesPerSecond = (heatingDegreesPerSecond + newHeatingDegreesPerSecond) / 2;
            }
            previousState = newState;
        }
    }

    ShiftableUpdate getFlexibilityUpdate() {
        updateState();

        Instant startAfter = getMayStart();
        Instant finishBefore = null;
        Duration cycleDuration = Duration.ofMinutes(15);

        ShiftableUpdate shiftableUpdate = Efi.build(ShiftableUpdate.class, deviceId);
        shiftableUpdate.setValidFrom(Efi.calendarOfInstant(startAfter));
        shiftableUpdate.setEndBefore(Efi.calendarOfInstant(finishBefore));
        SequentialProfiles profiles = new SequentialProfiles();

        // TODO MKE create flexibility
        SequentialProfile profile = new SequentialProfile();
        profile.setMaxIntervalBefore(Efi.DATATYPE_FACTORY.newDuration(0));
        profile.setSequenceNr(1);

        SequentialProfileAlternative sequentialProfileAlternative = new SequentialProfileAlternative();
        sequentialProfileAlternative.setAlternativeNr(1);
        ElectricityProfile electricityProfile = new ElectricityProfile();
        Element eProfileElement = new Element();
        eProfileElement.setDuration(Efi.DATATYPE_FACTORY.newDuration(cycleDuration.toMillis()));
        eProfileElement.setPower(0);
        electricityProfile.getElement().add(eProfileElement);
        sequentialProfileAlternative.setElectricityProfile(electricityProfile);
        profile.getSequentialProfileAlternatives().getSequentialProfileAlternative().add(sequentialProfileAlternative);

        profiles.getSequentialProfile().add(profile);
        shiftableUpdate.setSequentialProfiles(profiles);
        return shiftableUpdate;
    }

    /**
     * It would have been nice to actual measure this but for now it is a configurable value
     * 
     * @return
     */
    public int getPower() {
        updateState();
        if (newState.isCooling) {
            return config.getPower();
        }
        return 0;
    }
}
