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
package nl.technolution.batty;

import java.time.Instant;

import nl.technolution.DeviceId;
import nl.technolution.appliance.resourcemanager.IDeviceResourceHandler;
import nl.technolution.protocols.efi.Actuator;
import nl.technolution.protocols.efi.ActuatorBehaviour;
import nl.technolution.protocols.efi.ActuatorBehaviours;
import nl.technolution.protocols.efi.Actuators;
import nl.technolution.protocols.efi.CommodityEnum;
import nl.technolution.protocols.efi.DeviceClass;
import nl.technolution.protocols.efi.DeviceDescription;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.LeakageElement;
import nl.technolution.protocols.efi.LeakageFunction;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.Measurement.ElectricityMeasurement;
import nl.technolution.protocols.efi.RunningMode;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageRunningModes;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.Timer;
import nl.technolution.protocols.efi.Timers;
import nl.technolution.protocols.efi.util.Efi;

/**
 * Manages Sunny
 */
public class Batty implements IDeviceResourceHandler {

    private static final int ACTUATOR_ID = 1;
    private final DeviceId deviceId;

    public Batty(DeviceId devieId) {
        this.deviceId = devieId;
    }

    @Override
    public FlexibilityRegistration getRegistration() {
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

    @Override
    public FlexibilityUpdate getFlexibility() {
        StorageSystemDescription update = Efi.build(StorageSystemDescription.class, deviceId);
        update.setValidFrom(Efi.calendarOfInstant(Instant.now()));

        ActuatorBehaviours actuatorBehaviours = new ActuatorBehaviours();
        ActuatorBehaviour actuatorBehaviour = new ActuatorBehaviour();
        actuatorBehaviour.setActuatorId(1);
        StorageRunningModes runningModes = new StorageRunningModes();
        // TODO MKE create runningmode for batty
        RunningMode runningMode = null;
        runningModes.getDiscreteRunningModeOrContinuousRunningMode().add(runningMode);
        actuatorBehaviour.setRunningModes(runningModes);
        Timers timers = new Timers();
        Timer timer = new Timer();
        timer.setId(1);
        timer.setLabel("Charge period ofzo");
        timer.setDuration(Efi.DATATYPE_FACTORY.newDuration(1000));
        timers.getTimer().add(timer);
        actuatorBehaviour.setTimers(timers);
        actuatorBehaviours.getActuatorBehaviour().add(actuatorBehaviour);
        update.setActuatorBehaviours(actuatorBehaviours);

        LeakageFunction leakageFunction = new LeakageFunction();
        LeakageElement leakage = new LeakageElement();
        // TODO MKE set capacity battery
        leakage.setFillLevelLowerBound(10d);
        leakage.setFillLevelUpperBound(30000d);
        leakage.setLeakageRate(1d);
        leakageFunction.getLeakageElement().add(leakage);
        update.setLeakageBehaviour(leakageFunction);

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

    private double getCurrentUsage() {
        // TODO MKE read actual value in watt
        return 1000d;
    }

    @Override
    public DeviceId getDeviceId() {
        //
        return null;
    }
}