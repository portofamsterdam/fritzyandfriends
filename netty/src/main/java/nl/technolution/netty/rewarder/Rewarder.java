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
package nl.technolution.netty.rewarder;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.technolution.DeviceId;
import nl.technolution.TimedTaskService;

/**
 * 
 */
public class Rewarder extends TimedTaskService {

    /**
     * Constructor for {@link Rewarder} objects
     */
    public Rewarder() {
        // TODO MKE load rewards program
    }

    @Override
    public void init(ScheduledExecutorService executor) {
        executor.scheduleAtFixedRate(this::processRewards, 0, 1, TimeUnit.MINUTES);

    }

    private void processRewards() {

    }

    private double calculateReward(Instant ts, DeviceId buyer, DeviceId seller) {
        return 0d;
    }
}
