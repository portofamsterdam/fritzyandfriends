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

import nl.technolution.DeviceId;
import nl.technolution.apis.netty.DeviceCapacity;
import nl.technolution.apis.netty.INettyApi;
import nl.technolution.apis.netty.OrderReward;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.webservice.Endpoints;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApi;
import nl.technolution.fritzy.wallet.model.FritzyBalance;
import nl.technolution.fritzy.wallet.order.Orders;
import nl.technolution.fritzy.wallet.order.Record;
import nl.technolution.marketnegotiator.AbstractCustomerEnergyManager;
import nl.technolution.protocols.efi.ActuatorInstruction;
import nl.technolution.protocols.efi.ActuatorInstructions;
import nl.technolution.protocols.efi.Instruction;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageRegistration;
import nl.technolution.protocols.efi.StorageStatus;
import nl.technolution.protocols.efi.StorageUpdate;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class BatteryNegotiator extends AbstractCustomerEnergyManager<StorageRegistration, StorageUpdate> {

    private final BattyResourceManager resourceManager;
    private final FritzyApi market;

    private Double fillLevel;

    /**
     *
     * @param config config used for trading
     * @param resourceManager to control devices
     */
    public BatteryNegotiator(FritzyApi market, BattyResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.market = market;
    }

    @Override
    public Instruction flexibilityUpdate(StorageUpdate storageStatus) {
        if (storageStatus instanceof StorageStatus) {
            fillLevel = ((StorageStatus)storageStatus).getCurrentFillLevel();
        }
        return Efi.build(StorageInstruction.class, getDeviceId());

    }

    /**
     * Call periodicly to evaluate market changes
     */
    public void evaluate() {

        // Get balance
        FritzyBalance balance = market.balance();
        market.log(EEventType.BALANCE, balance.getEur().toPlainString(), null);

        // Get max capacity
        INettyApi netty = Endpoints.get(INettyApi.class);
        DeviceId deviceId = resourceManager.getDeviceId();
        DeviceCapacity deviceCapacity = netty.getCapacity(deviceId.getDeviceId());
        market.log(EEventType.LIMIT_ACTOR, Double.toString(deviceCapacity.getGridConnectionLimit()), null);

        Orders orders = market.orders().getOrders();
        for (Record record : orders.getRecords()) {
            WebOrder order = record.getOrder();
            if (!isInterestingOrder(order, balance, deviceCapacity)) {
                continue;
            }
            OrderReward reward = netty.getOrderReward(market.getAddress(), order.getHash());
            if (!checkAcceptOffer(order, reward)) {
                continue;
            }

            String txId = market.fillOrder(order.getHash());
            netty.claim(txId, reward.getRewardId());
            instructDevice(order);
        }
    }

    private boolean isInterestingOrder(WebOrder order, FritzyBalance balance, DeviceCapacity deviceCapacity) {
        // TODO MKE: check order content
        return true;
    }

    private boolean checkAcceptOffer(WebOrder order, OrderReward reward) {

        return false;
    }

    private void instructDevice(WebOrder order) {
        StorageInstruction instruction = Efi.build(StorageInstruction.class, getDeviceId());
        ActuatorInstructions actInstuctions = new ActuatorInstructions();
        ActuatorInstruction actInstruction = new ActuatorInstruction();
        actInstruction.setActuatorId(BattyResourceHelper.ACTUATOR_ID);
        actInstruction.setRunningModeId(EBattyInstruction.CHARGE.getRunningModeId());
        actInstruction.setStartTime(Efi.calendarOfInstant(Efi.getNextQuarter()));
        actInstuctions.getActuatorInstruction().add(actInstruction);
        instruction.setActuatorInstructions(actInstuctions);
        resourceManager.instruct(instruction);
    }

    public Double getFillLevel() {
        return fillLevel;
    }
}
