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
package nl.technolution.appliance.resourcemanager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.technolution.TimedTaskService;
import nl.technolution.marketnegotiator.ICustomerEnergyManager;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.Measurement;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public final class ResourceManagerService extends TimedTaskService {

    private final ICustomerEnergyManager cem;
    private final IDeviceResourceHandler device;

    /**
     * @param rm follows instruction from network
     * @param cem connection to network
     * @param device to handle
     */
    public ResourceManagerService(ICustomerEnergyManager cem, IDeviceResourceHandler device) {
        this.cem = cem;
        this.device = device;
    }

    @Override
    public void init(ScheduledExecutorService executor) {
        cem.flexibilityRegistration(device.getRegistration());
        executor.scheduleAtFixedRate(this::sendFlexibility, 0, 15, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(this::sendMeasurement, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void onShutdown() {
        cem.flexibilityRevoke(Efi.build(FlexibilityRevoke.class, device.getDeviceId()));
    }

    private void sendMeasurement() {
        Measurement measurement = device.getMeasurement();
        if (measurement != null) {
            cem.measurement(measurement);
        }
    }

    private void sendFlexibility() {
        FlexibilityUpdate update = device.getFlexibility();
        if (update != null) {
            cem.flexibilityUpdate(update);
        }
    }
}
