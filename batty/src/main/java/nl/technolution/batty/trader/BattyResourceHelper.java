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
package nl.technolution.batty.trader;

import java.time.Instant;

import nl.technolution.DeviceId;
import nl.technolution.batty.xstorage.cache.IMachineDataCacher;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.protocols.efi.Actuator;
import nl.technolution.protocols.efi.ActuatorBehaviour;
import nl.technolution.protocols.efi.ActuatorBehaviours;
import nl.technolution.protocols.efi.Actuators;
import nl.technolution.protocols.efi.CommodityEnum;
import nl.technolution.protocols.efi.DeviceClass;
import nl.technolution.protocols.efi.DeviceDescription;
import nl.technolution.protocols.efi.LeakageElement;
import nl.technolution.protocols.efi.LeakageFunction;
import nl.technolution.protocols.efi.StorageContinuousRunningMode;
import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement;
import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement.LowerBound;
import nl.technolution.protocols.efi.StorageContinuousRunningMode.ContinuousRunningModeElement.UpperBound;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageRunningModes;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.util.Efi;

/**
 * Manages Sunny
 */
public class BattyResourceHelper {


    public static final int ACTUATOR_ID = 1;

    static final double CAPACITY = 10000;
    static final String CHARGE_LABEL = "CHARGE";
    static final String DISCHARGE_LABEL = "DISCHARGE";

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

    StorageSystemDescription getStorageSystemDescription() {
        StorageSystemDescription update = Efi.build(StorageSystemDescription.class, deviceId);
        LeakageElement element = new LeakageElement();
        element.setLeakageRate(0d); // Ignore leagage for now
        LeakageFunction leakage = new LeakageFunction();
        leakage.getLeakageElement().add(element);
        update.setLeakageBehaviour(leakage);

        // one actuator with two runningmodes, charge and discharge
        ActuatorBehaviours actuatorBehaviours = new ActuatorBehaviours();
        ActuatorBehaviour battyBehaviour = new ActuatorBehaviour();
        battyBehaviour.setActuatorId(ACTUATOR_ID);

        // Battery can charge fast when empty and slow when full
        StorageContinuousRunningMode chargeRunningMode = getContinuousRunningMode(CHARGE_LABEL,
                EBattyInstruction.CHARGE.getRunningModeId(), 7000d, 3000d);
        // Battery can discharge slow when empty and fast when full
        StorageContinuousRunningMode dischargeRunningMode = getContinuousRunningMode(DISCHARGE_LABEL,
                EBattyInstruction.DISCHARGE.getRunningModeId(), 3000d, 7000d);

        StorageRunningModes runningModes = new StorageRunningModes();
        runningModes.getDiscreteRunningModeOrContinuousRunningMode().add(chargeRunningMode);
        runningModes.getDiscreteRunningModeOrContinuousRunningMode().add(dischargeRunningMode);

        battyBehaviour.setRunningModes(runningModes);
        actuatorBehaviours.getActuatorBehaviour().add(battyBehaviour);
        update.setActuatorBehaviours(actuatorBehaviours);
        return update;
    }

    private static StorageContinuousRunningMode getContinuousRunningMode(String label, int id, double low,
            double high) {
        StorageContinuousRunningMode continuousRunningMode = new StorageContinuousRunningMode();
        continuousRunningMode.setLabel(label);
        continuousRunningMode.setId(id);
        // Charge
        ContinuousRunningModeElement chargeRunningMode = new ContinuousRunningModeElement();
        // Used for complete SoC range of battery
        chargeRunningMode.setFillLevelLowerBound(0);
        chargeRunningMode.setFillLevelUpperBound(100);
        LowerBound lowBound = new LowerBound();
        lowBound.setElectricalPower(low);
        UpperBound highBound = new UpperBound();
        highBound.setElectricalPower(high);
        chargeRunningMode.setLowerBound(lowBound);
        chargeRunningMode.setUpperBound(highBound);

        continuousRunningMode.getContinuousRunningModeElement().add(chargeRunningMode);
        return continuousRunningMode;
    }
}