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
package nl.technolution.batty.xstorage;

import nl.technolution.batty.app.BattyConfig;
import nl.technolution.batty.xstorage.types.BmsData;
import nl.technolution.batty.xstorage.types.MachineData;
import nl.technolution.batty.xstorage.types.MachineInfo;
import nl.technolution.batty.xstorage.types.MeterInfo;
import nl.technolution.dropwizard.IService;

/**
 * 
 */
public interface IXStorageConnection extends IService<BattyConfig> {

    MachineInfo getMachineInfo() throws XStorageException;

    MachineData getMachineData() throws XStorageException;

    BmsData getBmsData() throws XStorageException;

    void charge(int percentage) throws XStorageException;

    void discharge(int percentage) throws XStorageException;

    void powerOn() throws XStorageException;

    void powerOff() throws XStorageException;

    MeterInfo getMeterInfo() throws XStorageException;

}
