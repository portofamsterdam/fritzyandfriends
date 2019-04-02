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

import nl.technolution.appliance.IDevice;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.Measurement;

/**
 * Handles Efi messages for specific device
 */
public interface IDeviceResourceHandler extends IDevice {

    /**
     * @return registration
     */
    FlexibilityRegistration getRegistration();

    /**
     * @return flexibility
     */
    FlexibilityUpdate getFlexibility();

    /**
     * @return measurement
     */
    Measurement getMeasurement();
}
