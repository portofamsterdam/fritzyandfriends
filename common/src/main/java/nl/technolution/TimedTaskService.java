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
package nl.technolution;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.lifecycle.Managed;

/**
 * 
 */
public abstract class TimedTaskService implements Managed {

    private static final Logger LOG = LoggerFactory.getLogger(TimedTaskService.class);

    private ScheduledExecutorService executor;

    @Override
    public final void start() throws Exception {
        executor = Executors.newSingleThreadScheduledExecutor();
        init(executor);
    }

    @Override
    public final void stop() throws Exception {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        if (!executor.isTerminated()) {
            List<Runnable> failedTasks = executor.shutdownNow();
            if (failedTasks.size() != 0) {
                LOG.error("Unable to shutdown tasks: {}", failedTasks);
            }
        }
        onShutdown();
    }

    /**
     * Calls after executor is created
     * 
     * @param executor to add tasks to
     */
    public abstract void init(ScheduledExecutorService executor);


    /**
     * Called after tasks are stopped
     */
    public void onShutdown() {
        // default empty implementation
    }
}
