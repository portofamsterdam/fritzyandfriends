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

import nl.technolution.dropwizard.services.IService;
import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.Measurement;

/**
 * Handles Efi messages for specific device
 * 
 * @param <T> type used to init service
 */
public interface IDeviceResourceHandler<T> extends IService<T> {

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
