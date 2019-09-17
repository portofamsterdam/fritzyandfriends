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
package nl.technolution.netty.usagescanner;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import nl.technolution.Log;
import nl.technolution.dashboard.EEventType;
import nl.technolution.dropwizard.services.Services;
import nl.technolution.dropwizard.tasks.ITaskRunner;
import nl.technolution.dropwizard.tasks.TimedTask;
import nl.technolution.fritzy.gen.model.WebOrder;
import nl.technolution.fritzy.gen.model.WebUser;
import nl.technolution.fritzy.wallet.IFritzyApi;
import nl.technolution.fritzy.wallet.IFritzyApiFactory;
import nl.technolution.fritzy.wallet.event.EventLogger;
import nl.technolution.fritzy.wallet.model.ApiEvent;
import nl.technolution.fritzy.wallet.model.EContractAddress;
import nl.technolution.netty.rewarder.IRewardService;
import nl.technolution.netty.supplylimit.IGridCapacityManager;
import nl.technolution.protocols.efi.util.Efi;

/**
 * 
 */
@TimedTask(period = 1, unit = TimeUnit.MINUTES)
public class UsageScanner implements ITaskRunner {

    private final Logger log = Log.getLogger();

    private IFritzyApi api;

    @Override
    public void execute() {
        Instant nextQuarter = Efi.getNextQuarter();
        Instant previousQuarter = nextQuarter.minus(15, ChronoUnit.MINUTES);

        IFritzyApi fritzyApi = getFritzyApi();
        EventLogger eventlogger = new EventLogger(fritzyApi);

        double groupConnectionLimit = Services.get(IGridCapacityManager.class).getGroupConnectionLimit();
        eventlogger.logLimitTotal(groupConnectionLimit);

        double badUsage = 0.0d;

        for (ApiEvent event : fritzyApi.getEvents(previousQuarter, nextQuarter).getEvents()) {
            if (event.getTag().equals(EEventType.ORDER_ACCEPT.getTag())) {
                badUsage += getUsage(event);
            }
            if (badUsage > groupConnectionLimit) {
                // Last order exceeded limit. This must be a nonlocal trade made by exxy and filled by our naughty actor
                eventlogger.logLimitExceeded(getTakerName(event));
            }
        }
    }

    private String getTakerName(ApiEvent event) {
        WebOrder order = getFritzyApi().order(event.getMsg());
        if (order == null) {
            log.error("Could not find order");
            return "unknown";
        }
        for (WebUser u : getFritzyApi().getUsers()) {
            if (u.getAddress().equals(order.getTakerAddress())) {
                return u.getName();
            }
        }
        return "unknown";
    }

    private double getUsage(ApiEvent event) {
        WebOrder order = getFritzyApi().order(event.getMsg());
        if (order == null) {
            log.error("Could not find order");
            return 0.0d;
        }

        String makerUsername = null;
        String takerUsername = null;
        for (WebUser u : getFritzyApi().getUsers()) {
            if (u.getAddress().equals(order.getMakerAddress())) {
                makerUsername = u.getName();
                continue;
            }
            if (u.getAddress().equals(order.getTakerAddress())) {
                takerUsername = u.getName();
                continue;
            }
        }
        
        if (makerUsername == null || takerUsername == null) {
            log.error("Incomplete order {}", event.getMsg());
            return 0.0d;
        }
        
        
        IRewardService rewardService = Services.get(IRewardService.class);
        if (!rewardService.isLocal(makerUsername, takerUsername)) {
            // only count the order tokens if they're KWH. 
            if (order.getMakerAssetData().equalsIgnoreCase(EContractAddress.KWH.name())) {
                return new BigDecimal(order.getMakerAssetAmount()).doubleValue();
            }
            if (order.getTakerAssetData().equalsIgnoreCase(EContractAddress.KWH.name())) {
                return new BigDecimal(order.getTakerAssetAmount()).doubleValue();
            }
            log.error("Token kWh not found in order {}, maker {}, taker {}", event.getMsg(), order.getMakerAssetData(), 
                    order.getTakerAssetData());
        }
        return 0;
    }

    private IFritzyApi getFritzyApi() {
        if (api == null) {
            api = Services.get(IFritzyApiFactory.class).build();
        }
        return api;
    }

}
