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

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.util.ICustomerEnergyManager;

/**
 * 
 */
public class ResourceManagerService implements IResourceManagerService {

    private DeviceId deviceId;
    private IDeviceResourceHandler<?> devieResource;
    private ICustomerEnergyManager<FlexibilityRegistration, FlexibilityUpdate> cem;

    @Override
    public void init(DeviceId deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public void update() {

        //

    }
}
