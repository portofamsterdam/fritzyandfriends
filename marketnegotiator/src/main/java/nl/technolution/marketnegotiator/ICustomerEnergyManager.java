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
package nl.technolution.marketnegotiator;

import nl.technolution.protocols.efi.FlexibilityRegistration;
import nl.technolution.protocols.efi.FlexibilityRevoke;
import nl.technolution.protocols.efi.FlexibilityUpdate;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.InstructionStatusUpdate;
import nl.technolution.protocols.efi.Measurement;

/**
 * 
 */
public interface ICustomerEnergyManager<T extends FlexibilityRegistration, S extends FlexibilityUpdate> {

    /**
     * As soon as a resource manager becomes available to the CEM, it will send a message that is derived from this
     * FlexibilityRegistration class. This message is used to inform the CEM about the capabilities of the resource.
     * 
     * @param flexibilityRegistration registration
     */
    void flexibilityRegistration(T flexibilityRegistration);

    /**
     * After the registration of the Resource Manager with the CEM, the Resource Manage can now start sending its
     * current flexibility options.
     * 
     * @param update update
     * @return instruction what to do
     */
    Instruction flexibilityUpdate(S update);

    /**
     * After a Resource Manager has received an Instruction message, it is good practice to provide feedback to the CEM
     * about the follow up actions. This can be done via this message. Multiple InstructionStatusUpdate messages may be
     * sent in response to a single Instruction. All flexibility categories use this InstructionStatusUpdate class;
     * there are no specific derivations in use.
     * 
     * @param instructionStatusUpdate
     */
    void instructionStatusUpdate(InstructionStatusUpdate instructionStatusUpdate);

    /**
     * A Resource Manager may revoke FlexibilityUpdate messages that it already sent to the CEM. After sending this
     * FlexibiltyRevoke message all FlexibilityUpdate messages that were sent before it are rendered invalid. The
     * FlexibilityRegistration message does remain valid. All flexibility categories use this FlexibilityRevoke class;
     * there are no specific derivations in use.
     * 
     * @param revocation
     */
    void flexibilityRevoke(FlexibilityRevoke revocation);

    /**
     * In principal the CEM knows what a Resource Manager is doing based on FlexibilityUpdate and
     * InstructionStatusUpdate messages. In reality however the behaviour of the Resource Manager may deviate from the
     * information it sent in these messages. This can have different causes, such as manual user intervention of the
     * device managed by the Resource Manager or a lack of precision in forecasting information.
     * 
     * If available a Resource Manager may send Measurement messages to the CEM, so that the CEM is informed of what is
     * actually happening and can determine whether there are signific ant differences to what it expected.
     * 
     * @param measurement contains electricityMeasurement, gasMeasurement or heatMeasurement.
     */
    void measurement(Measurement measurement);
}
