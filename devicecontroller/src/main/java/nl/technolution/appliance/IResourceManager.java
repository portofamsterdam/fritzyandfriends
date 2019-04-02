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
package nl.technolution.appliance;

import nl.technolution.DeviceId;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionRevoke;

/**
 */
public interface IResourceManager {

    /**
     * Is used by a device to identify itself
     * 
     * @return id of device
     */
    DeviceId getDeviceId();

    /**
     * @param instruction
     */
    void instruct(Instruction instruction);

    /**
     * The CEM may revoke an Instruction it sent earlier. In that case it will send this InstructionRevoke message
     * 
     * @param instructionRevoke what to do
     */
    void instructionRevoke(InstructionRevoke instructionRevoke);
}
