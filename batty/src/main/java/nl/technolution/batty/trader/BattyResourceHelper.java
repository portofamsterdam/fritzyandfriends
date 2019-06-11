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

import java.math.BigDecimal;
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
import nl.technolution.protocols.efi.StorageDiscreteRunningMode;
import nl.technolution.protocols.efi.StorageDiscreteRunningMode.DiscreteRunningModeElement;
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
        int percFilled = getMachineData().getSoc();

        double fillLevel = ((double)percFilled / 100d) * CAPACITY;
        update.setCurrentFillLevel(fillLevel);
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

        ActuatorBehaviours actuatorBehaviours = new ActuatorBehaviours();
        ActuatorBehaviour battyBehaviour = new ActuatorBehaviour();
        battyBehaviour.setActuatorId(ACTUATOR_ID);

        StorageRunningModes runningModes = new StorageRunningModes();

        // Charge running mode
        StorageDiscreteRunningMode chargeRunningMode = new StorageDiscreteRunningMode();
        chargeRunningMode.setId(1);
        chargeRunningMode.setLabel(EBattyInstruction.CHARGE.name());

        double chargeRate = 1000d;
        DiscreteRunningModeElement chargeElement = new DiscreteRunningModeElement();
        chargeElement.setFillLevelLowerBound(0);
        chargeElement.setFillLevelUpperBound(CAPACITY);
        chargeElement.setElectricalPower(chargeRate); // TODO MKE implement charge rate
        chargeElement.setRunningCost(BigDecimal.valueOf(0)); // assume running device is free
        chargeElement.setFillingRate(chargeRate / 3600d); // NOTE MKE: for now assume 100% efficiency
        chargeRunningMode.getDiscreteRunningModeElement().add(chargeElement);

        // Discharge running mode
        StorageDiscreteRunningMode dischargeRunningMode = new StorageDiscreteRunningMode();
        dischargeRunningMode.setId(2);
        dischargeRunningMode.setLabel(EBattyInstruction.DISCHARGE.name());

        double dischargeRate = -1000d;
        DiscreteRunningModeElement dischargeElement = new DiscreteRunningModeElement();
        dischargeElement.setFillLevelLowerBound(0);
        dischargeElement.setFillLevelLowerBound(CAPACITY);
        dischargeElement.setElectricalPower(dischargeRate); // TODO MKE implement discharge rate
        dischargeElement.setRunningCost(BigDecimal.valueOf(0)); // assume running device is free
        dischargeElement.setFillingRate(dischargeRate / 3600d); // NOTE MKE: for now assume 100% efficiency
        dischargeRunningMode.getDiscreteRunningModeElement().add(dischargeElement);

        runningModes.getDiscreteRunningModeOrContinuousRunningMode().add(chargeRunningMode);
        runningModes.getDiscreteRunningModeOrContinuousRunningMode().add(dischargeRunningMode);

        battyBehaviour.setRunningModes(runningModes);
        actuatorBehaviours.getActuatorBehaviour().add(battyBehaviour);
        update.setActuatorBehaviours(actuatorBehaviours);
        return update;
    }
}