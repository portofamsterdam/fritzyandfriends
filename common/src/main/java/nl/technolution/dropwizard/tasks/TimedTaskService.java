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
package nl.technolution.dropwizard.tasks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

import org.slf4j.Logger;

import io.dropwizard.lifecycle.Managed;
import nl.technolution.Log;
import nl.technolution.core.resources.TypeFinder;
import nl.technolution.dropwizard.FritzyDropWizardApp;

/**
 * Manages task with @Timed annotation
 */
public final class TimedTaskService implements Managed {

    private static final Logger LOG = Log.getLogger();

    private ScheduledExecutorService executor;

    @Override
    public void start() throws Exception {
        Preconditions.checkArgument(executor == null || executor.isTerminated());
        executor = Executors.newSingleThreadScheduledExecutor();

        List<Class<? extends TimedTask>> tasks = TypeFinder
                .findImplementingClasses(FritzyDropWizardApp.PKG, TimedTask.class);
        
        for (Class<? extends TimedTask> taskClass : tasks) {
            TimedTask task = taskClass.getAnnotation(TimedTask.class);
            ChronoUnit unit = convert(task.unit());
            long delay = Instant.now().until(LocalDateTime.now().truncatedTo(unit).plus(1, unit), ChronoUnit.SECONDS);
            Preconditions.checkArgument(Runnable.class.isAssignableFrom(taskClass));
            Runnable runner = Runnable.class.cast(taskClass.newInstance());
            executor.scheduleAtFixedRate(runner, delay, task.period() * unit.getDuration().getSeconds(),
                    TimeUnit.SECONDS);
        }
    }

    @Override
    public void stop() throws Exception {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        if (!executor.isTerminated()) {
            executor.shutdownNow().forEach(r -> LOG.error("Task {} didn't shutdown", r));
        }
    }

    private static ChronoUnit convert(TimeUnit tu) {
        if (tu == null) {
            return null;
        }
        switch (tu) {
        case DAYS:
            return ChronoUnit.DAYS;
        case HOURS:
            return ChronoUnit.HOURS;
        case MINUTES:
            return ChronoUnit.MINUTES;
        case SECONDS:
            return ChronoUnit.SECONDS;
        case MICROSECONDS:
            return ChronoUnit.MICROS;
        case MILLISECONDS:
            return ChronoUnit.MILLIS;
        case NANOSECONDS:
            return ChronoUnit.NANOS;
        default:
            throw new UnsupportedOperationException();
        }
    }
}
