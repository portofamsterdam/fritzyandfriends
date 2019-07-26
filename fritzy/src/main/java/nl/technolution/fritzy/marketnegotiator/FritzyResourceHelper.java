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
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;

import nl.technolution.DeviceId;
import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.app.FritzyConfig;
import nl.technolution.fritzy.io.IIoFactory;
import nl.technolution.protocols.efi.Actuator;
import nl.technolution.protocols.efi.ActuatorBehaviour;
import nl.technolution.protocols.efi.ActuatorBehaviours;
import nl.technolution.protocols.efi.ActuatorStatus;
import nl.technolution.protocols.efi.Actuators;
import nl.technolution.protocols.efi.CommodityEnum;
import nl.technolution.protocols.efi.DeviceClass;
import nl.technolution.protocols.efi.DeviceDescription;
import nl.technolution.protocols.efi.LeakageElement;
import nl.technolution.protocols.efi.LeakageFunction;
import nl.technolution.protocols.efi.StorageDiscreteRunningMode;
import nl.technolution.protocols.efi.StorageDiscreteRunningMode.DiscreteRunningModeElement;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageRunningModes;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.StorageUpdate;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class FritzyResourceHelper {
    public static final int ACTUATOR_ID = 1;
    private static final Logger LOG = Log.getLogger();

    private FritzyConfig config;
    private DeviceId deviceId;
    private double coolingDegreesPerSecond = 0;
    private double heatingDegreesPerSecond = 0;

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

    StorageRegistration getRegistration() {
        StorageRegistration reg = Efi.build(StorageRegistration.class, deviceId);
        DeviceDescription description = new DeviceDescription();
        description.setDeviceClass(DeviceClass.REFRIGERATOR);
        reg.setDeviceDescription(description);

        reg.setFillLevelLabel("Temperature");
        reg.setFillLevelUnit("°C");

        Actuators actuators = new Actuators();
        Actuator actuator = new Actuator();
        actuator.setId(ACTUATOR_ID);
        actuator.getSupportedCommodity().add(CommodityEnum.ELECTRICITY);
        actuators.getActuator().add(actuator);
        reg.setActuators(actuators);

        reg.setInstructionProcessingDelay(Efi.DATATYPE_FACTORY.newDuration(50));
        return reg;
    }

    StorageSystemDescription getStorageSystemDescription() {
        StorageSystemDescription update = Efi.build(StorageSystemDescription.class, deviceId);
        LeakageElement element = new LeakageElement();
        element.setFillLevelLowerBound(config.getMinTemp());
        element.setFillLevelUpperBound(config.getMaxTemp());
        // make negative because fillLevel (temperature) goes up due to leakages
        // TODO WHO: how should CEM know when leakage is applicable?? Ony in idle (here 'OFF') running mode! For now
        // ignore it...
        element.setLeakageRate(-config.getLeakageRate());
        LeakageFunction leakage = new LeakageFunction();
        leakage.getLeakageElement().add(element);
        update.setLeakageBehaviour(leakage);

        ActuatorBehaviours actuatorBehaviours = new ActuatorBehaviours();
        ActuatorBehaviour fritzyBehaviour = new ActuatorBehaviour();
        fritzyBehaviour.setActuatorId(ACTUATOR_ID);

        StorageRunningModes runningModes = new StorageRunningModes();

        // On (cooling) running mode
        StorageDiscreteRunningMode onRunningMode = new StorageDiscreteRunningMode();
        onRunningMode.setId(EFritzyRunningMode.ON.getRunningModeId());
        onRunningMode.setLabel(EFritzyRunningMode.ON.name());

        DiscreteRunningModeElement onElement = new DiscreteRunningModeElement();
        onElement.setFillLevelLowerBound(config.getMinTemp());
        onElement.setFillLevelUpperBound(config.getMaxTemp());
        onElement.setElectricalPower(config.getPower());
        onElement.setRunningCost(BigDecimal.valueOf(0)); // assume running device is free
        onElement.setFillingRate(config.getCoolingSpeed());
        onRunningMode.getDiscreteRunningModeElement().add(onElement);

        // Off (heating up) running mode
        StorageDiscreteRunningMode offRunningMode = new StorageDiscreteRunningMode();
        offRunningMode.setId(EFritzyRunningMode.OFF.getRunningModeId());
        offRunningMode.setLabel(EFritzyRunningMode.OFF.name());

        DiscreteRunningModeElement offElement = new DiscreteRunningModeElement();
        offElement.setFillLevelLowerBound(config.getMinTemp());
        offElement.setFillLevelUpperBound(config.getMaxTemp());
        offElement.setElectricalPower(0d);
        offElement.setRunningCost(BigDecimal.valueOf(0)); // assume running device is free
        offElement.setFillingRate(config.getLeakageRate());
        offRunningMode.getDiscreteRunningModeElement().add(offElement);

        runningModes.getDiscreteRunningModeOrContinuousRunningMode().add(onRunningMode);
        runningModes.getDiscreteRunningModeOrContinuousRunningMode().add(offRunningMode);

        // TODO: add timers to prevent continues switching between on/off (not needed when working with 15 minute
        // periods)

        fritzyBehaviour.setRunningModes(runningModes);
        actuatorBehaviours.getActuatorBehaviour().add(fritzyBehaviour);
        update.setActuatorBehaviours(actuatorBehaviours);
        return update;
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
                if (coolingDegreesPerSecond == 0) {
                    coolingDegreesPerSecond = newCoolingDegreesPerSecond;
                } else {
                    coolingDegreesPerSecond = (coolingDegreesPerSecond + newCoolingDegreesPerSecond) / 2;
                }
            } else {
                double newHeatingDegreesPerSecond = (newState.temperature - previousState.temperature) /
                        Duration.between(previousState.instant, newState.instant).getSeconds();
                if (heatingDegreesPerSecond == 0) {
                    heatingDegreesPerSecond = newHeatingDegreesPerSecond;
                } else {
                    heatingDegreesPerSecond = (heatingDegreesPerSecond + newHeatingDegreesPerSecond) / 2;
                }
            }
        }

        LOG.info(String.format(
                "isCooling: %b (previous: %b)  temperature: %.6f °C  cooling: %.6f °C/s heatup: %.6f °C/s",
                newState.isCooling, previousState.isCooling, newState.temperature, coolingDegreesPerSecond,
                heatingDegreesPerSecond));

        previousState = newState;
    }

    private void outOflimitsProtection(FritzyController controller) {
        if (newState.temperature > (config.getMaxTemp() + 0.5) && !newState.isCooling) {
            controller.on();
            updateState();
            LOG.warn("OutOflimitsProtection: turned Fritzy on without instruction because too hot and not cooling.");
        }
        if (newState.temperature < (config.getMinTemp() - 0.5) && newState.isCooling) {
            controller.off();
            updateState();
            LOG.warn("OutOflimitsProtection: turned Fritzy off without instruction because too cold and still " +
                    "cooling.");
        }
    }

    /**
     * @param controller
     * @return
     */
    public StorageUpdate getFlexibilityUpdate(FritzyController controller) {
        StorageStatus update = Efi.build(StorageStatus.class, deviceId);

        updateState();
        outOflimitsProtection(controller);

        update.setValidFrom(Efi.calendarOfInstant(Instant.now()));
        update.setCurrentFillLevel(newState.temperature);
        ActuatorStatus status = new ActuatorStatus();
        status.setActuatorId(ACTUATOR_ID);
        status.setCurrentRunningMode(EFritzyRunningMode.fromIsCooling(newState.isCooling).getRunningModeId());
        update.getActuatorStatuses().getActuatorStatus().add(status);
        return update;
    }

    /**
     * It would have been nice to actual measure this but for now it is a configurable value
     * 
     * @return
     */
    public double getPower() {
        updateState();
        if (newState.isCooling) {
            return config.getPower();
        }
        return 0;
    }
}
