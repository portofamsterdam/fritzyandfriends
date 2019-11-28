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

import java.math.BigDecimal;
import java.time.Instant;

import com.google.common.base.Preconditions;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.wallet.FritzyApiException;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.OrderHelper;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.protocols.efi.ActuatorInstruction;
import nl.technolution.protocols.efi.ActuatorInstructions;
import nl.technolution.protocols.efi.InstructionRevoke;
import nl.technolution.protocols.efi.StorageInstruction;
import nl.technolution.protocols.efi.StorageSystemDescription;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
public class OrderExecutor {

    private final Logger log = Log.getLogger();
    private final String orderHash;

    private Instant startTs;
    private boolean orderAccepted = false;

    private boolean orderExecutionStarted = false;
    private Instant stopExecutionTs;

    /**
     * Constructor for {@link OrderExecutor} objects
     *
     * @param startTs
     * @param orderHash
     */
    public OrderExecutor(String orderHash) {
        this.orderHash = orderHash;
    }

    /**
     * evaluate order
     *
     * @param system batty
     * @param resourceManager controller
     * @return true if order is still relevant
     * @throws FritzyApiException
     */
    public EOrderCommand evaluate(BattyResourceManager resourceManager, StorageSystemDescription system,
            double fillLevel) throws FritzyApiException {
        log.info("Processing order state {}", orderHash);
        IFritzyApi market = Services.get(IFritzyApiFactory.class).build();
        WebOrder order = market.order(orderHash);
        if (order == null) {
            log.error("order {} not found", orderHash);
            return EOrderCommand.FINISHED; // order is gone
        }

        if (!OrderHelper.isAccepted(order)) {
            log.debug("order {} not accepted yet", orderHash);
            return EOrderCommand.NONE; // Order is still open
        }

        // if taker is set, order is accepted
        if (!orderAccepted) {
            Instant nextQuarter = Efi.getNextQuarter();
            log.info("order {} accepted, starting {}", orderHash, nextQuarter);
            orderAccepted = true;
            startTs = nextQuarter;
        }

        if (Instant.now().isAfter(startTs) && !orderExecutionStarted) {
            log.info("order {} start", orderHash);
            orderExecutionStarted = true;
            intructStart(order, resourceManager, system, market.getAddress(), fillLevel);
            return EOrderCommand.START;
        }

        if (stopExecutionTs != null && Instant.now().isAfter(stopExecutionTs)) {
            log.info("order {} finished", orderHash);
            resourceManager.instructionRevoke(Efi.build(InstructionRevoke.class, resourceManager.getDeviceId()));
            return EOrderCommand.FINISHED;
        }
        return EOrderCommand.NONE;
    }

    private void intructStart(WebOrder order, BattyResourceManager resourceManager, StorageSystemDescription system,
            String battyAddress, double fillLevel) {
        Preconditions.checkNotNull(battyAddress);

        if (order.getMakerAddress().equals(battyAddress)) {
            if (order.getMakerAssetData().equals(EContractAddress.KWH.getContractName())) {
                // Discharge
                log.info("Maker is batty, he's making {} so discharging", order.getMakerAssetData());
                instructDischarge(order.getMakerAssetAmount(), resourceManager, system, fillLevel);
            } else if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName())) {
                // Charge
                log.info("Maker is batty, taker gets {} so charging", order.getTakerAssetData());
                instructCharge(order.getMakerAssetAmount(), resourceManager, system, fillLevel);
            }
        }

        if (order.getTakerAddress().equals(battyAddress)) {
            if (order.getTakerAssetData().equals(EContractAddress.KWH.getContractName())) {
                // Discharge
                log.info("Taker is batty, being taken is {} so charging", order.getTakerAssetData());
                instructDischarge(order.getMakerAssetAmount(), resourceManager, system, fillLevel);
            } else if (order.getMakerAssetData().equals(EContractAddress.KWH.getContractName())) {
                log.info("Taker is batty, maker makes {} so discharging", order.getTakerAssetData());
                // Charge
                instructCharge(order.getMakerAssetAmount(), resourceManager, system, fillLevel);
            }
        }
    }

    private void instructCharge(String amount, BattyResourceManager resourceManager,
            StorageSystemDescription system, double fillLevel) {
        resourceManager.instruct(getActuatorInstruction(resourceManager, EBattyInstruction.CHARGE));
        log.info("Started charging, fillLevel is {}", fillLevel);
        double wattPerQuarter = BatteryNegotiator
                .getWattPerQuarterFromRunningMode(BattyResourceHelper.CHARGE_LABEL, system, fillLevel);
        double kWPerQuarter = wattPerQuarter / 1000d;
        double toCharge = new BigDecimal(amount).doubleValue();
        if (toCharge > kWPerQuarter) {
            log.error("Cannot charge {} in a quarter, max is {}. Doing as much as batty can", toCharge, kWPerQuarter);
            stopExecutionTs = startTs.plusSeconds(900);
        } else {
            double endTs = 900d * (toCharge / kWPerQuarter);
            log.debug("Charging for {} seconds, {} to discharge at {} p.q.", endTs, toCharge, kWPerQuarter);
            Preconditions.checkArgument(endTs >= 0d && endTs <= 900d);
            stopExecutionTs = startTs.plusSeconds((int)endTs);
        }
        log.info("Charging till {}", stopExecutionTs);

    }

    private void instructDischarge(String amount, BattyResourceManager resourceManager,
            StorageSystemDescription system, double fillLevel) {
        resourceManager.instruct(getActuatorInstruction(resourceManager, EBattyInstruction.DISCHARGE));
        log.info("Started discharging, fillLevel is {}", fillLevel);
        double wattPerQuarter = BatteryNegotiator
                .getWattPerQuarterFromRunningMode(BattyResourceHelper.DISCHARGE_LABEL, system, fillLevel);
        double kWPerQuarter = wattPerQuarter / 1000d;
        double toDischarge = new BigDecimal(amount).doubleValue();
        if (toDischarge > kWPerQuarter) {
            log.error("Cannot discharge {} in a quarter, max is {}. Doing as much as batty can", toDischarge,
                    kWPerQuarter);
            stopExecutionTs = startTs.plusSeconds(900);
        } else {
            double endTs = 900d * (toDischarge / kWPerQuarter);
            log.debug("Charging for {} seconds, {} to discharge at {} p.q.", endTs, toDischarge, kWPerQuarter);
            Preconditions.checkArgument(endTs >= 0d && endTs <= 900d);
            stopExecutionTs = startTs.plusSeconds((int)endTs);
        }
        log.info("Discharging till {}", stopExecutionTs);

    }
    /**
     * @param resourceManager
     * @param battyInstruction to set
     * @return
     */
    private StorageInstruction getActuatorInstruction(BattyResourceManager resourceManager,
            EBattyInstruction battyInstruction) {
        StorageInstruction instruction = Efi.build(StorageInstruction.class, resourceManager.getDeviceId());
        ActuatorInstructions actInstructions = new ActuatorInstructions();
        ActuatorInstruction instrcution = new ActuatorInstruction();
        instrcution.setRunningModeId(battyInstruction.getRunningModeId());
        instrcution.setRunningModeFactor(1d);
        instrcution.setActuatorId(BattyResourceHelper.ACTUATOR_ID);
        instrcution.setStartTime(Efi.calendarOfInstant(Instant.now()));
        actInstructions.getActuatorInstruction().add(instrcution);
        instruction.setActuatorInstructions(actInstructions);
        return instruction;
    }

    public Instant getStartTs() {
        return startTs;
    }

    public void setStartTs(Instant startTs) {
        this.startTs = startTs;
    }

    public void setStopExecutionTs(Instant stopExecutionTs) {
        this.stopExecutionTs = stopExecutionTs;
    }

    public Instant getStopExecutionTs() {
        return stopExecutionTs;
    }
}
