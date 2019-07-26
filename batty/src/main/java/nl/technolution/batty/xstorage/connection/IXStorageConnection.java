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
package nl.technolution.batty.xstorage.connection;

import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineInfo;
import nl.technolution.batty.xstorage.types.MeterInfo;

/**
 * 
 */
public interface IXStorageConnection {

    /**
     * Get machine info like serial, firmware etc.
     * 
     * @return Machine information
     * @throws XStorageException
     */
    MachineInfo getMachineInfo() throws XStorageException;

    /**
     * Get machine data, battery current, grid current, state of charge etc.
     * 
     * @return Machine Da
     * @throws XStorageException
     */
    MachineData getMachineData() throws XStorageException;

    /**
     * Get Battery management system data
     * 
     * @return BMS data object
     * @throws XStorageException
     */
    BmsData getBmsData() throws XStorageException;

    /**
     * Start charging with a relative chargerate
     * 
     * @param percentage 0 - 100%
     * @throws XStorageException when command cannot be executed
     */
    void charge(int percentage) throws XStorageException;

    /**
     * Start discharging with a relative dischargerate
     * 
     * @param percentage 0 - 100%
     * @throws XStorageException when command cannot be executed
     */
    void discharge(int percentage) throws XStorageException;

    /**
     * Power on the device. After poweron device can accept charge and discharge commands
     * 
     * @throws XStorageException when device cannot be powered on
     */
    void powerOn() throws XStorageException;

    /**
     * Power of device. Battery cannot longer accept charge and discharge commands
     * 
     * @throws XStorageException
     */
    void powerOff() throws XStorageException;

    /**
     * Get information from connection electricity meters
     * 
     * @return information about energy meters
     * @throws XStorageException
     */
    MeterInfo getMeterInfo() throws XStorageException;


}
