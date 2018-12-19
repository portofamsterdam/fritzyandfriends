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
package nl.technolution.protocols.efi.util;

/**
 * 
 */
public interface IEnergyApp {

    /**
     * As soon as an appliance becomes available to an energy app (a term that is used interchangeably with Smart Grid
     * Application, it will send a ControlSpaceRegistration message. This message is used to inform the energy app about
     * the capabilities of the appliance.
     * 
     * @param controlSpaceRegistration information about the connecting device
     */
    // void controlSpaceRegistration(ControlSpaceRegistration controlSpaceRegistration);

    /**
     * This message contains information about the flexibility an appliance has to offer
     * 
     * @param controlSpaceUpdate update
     * @return contains instructions on how to use (or fix) the flexibility described in the ControlSpaceUpdate
     */
    // Allocation controlSpaceUpdate(ControlSpaceUpdate controlSpaceUpdate);

    /**
     * After an appliance driver received an Allocation message, it is good practice to provide feedback to the energy
     * app on the follow up actions.
     * 
     * @param allocationStatusUpdate Allocation message that this update refers to
     * @return contains instructions on how to use (or fix) the flexibility described in the ControlSpaceUpdate
     */
    // Allocation allocationUpdate(AllocationStatusUpdate allocationStatusUpdate);

    /**
     * An appliance driver can revoke an already sent ControlSpaceUpdate message by sending the ControlSpaceRevoke
     * message. After sending the message every received ControlSpaceUpdate should be removed by the energy app, only
     * the registration message is valid afterwards.
     * 
     * @param controlSpaceRevoke
     */
    // void controlSpaceRevoke(ControlSpaceRevoke controlSpaceRevoke);
}
