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
import nl.technolution.dropwizard.services.Services;

/**
 * Manages task with @Timed annotation
 */
public final class TimedTaskService implements Managed {

    private static final Logger LOG = Log.getLogger();

    private ScheduledExecutorService executor;

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void start() throws Exception {
        Preconditions.checkArgument(executor == null || executor.isTerminated());
        executor = Executors.newSingleThreadScheduledExecutor();

        List<Class<? extends ITaskRunner>> timeTaskAnnotationClasses = TypeFinder
                .findImplementingClasses(FritzyDropWizardApp.PKG, ITaskRunner.class);

        LOG.info("Found {} tasks", timeTaskAnnotationClasses.size());
        
        for (Class<? extends ITaskRunner> timedTaskAnnotatedClass : timeTaskAnnotationClasses) {
            LOG.info("Found task: {}", timedTaskAnnotatedClass);
            // Create parameter to run scheduled task
            TimedTask timedTaskAnnotation = timedTaskAnnotatedClass.getAnnotation(TimedTask.class);
            Preconditions.checkNotNull(timedTaskAnnotation, "Annotation @TimedTask not found");

            ChronoUnit unit = convert(timedTaskAnnotation.unit());
            long delay = LocalDateTime.now().until(LocalDateTime.now().truncatedTo(unit).plus(1, unit),
                    ChronoUnit.SECONDS);
            long periodSec = timedTaskAnnotation.period() * unit.getDuration().getSeconds();


            // Find the implementation of ITask to run and register
            Class taskInterface = null;
            for (Class<?> interfaceClazz : timedTaskAnnotatedClass.getInterfaces()) {
                if (ITaskRunner.class.isAssignableFrom(interfaceClazz)) {
                    taskInterface = interfaceClazz;
                    break;
                }
            }
            Preconditions.checkNotNull(taskInterface, "TimedTask does not implement ITaskRunner");

            // Build an instance to run in scheduler
            Class<? extends ITaskRunner> typedClazz = (Class<? extends ITaskRunner>)timedTaskAnnotatedClass;
            ITaskRunner task = typedClazz.newInstance();
            executor.scheduleAtFixedRate(new SafeTaskRunnable(task), delay, periodSec, TimeUnit.SECONDS);
            LOG.info("Starting task {} in {} seconds", typedClazz.getSimpleName(), delay);

            // Register tasks in Service
            Services.put(taskInterface, taskInterface.cast(task));
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

    private final class SafeTaskRunnable implements Runnable {
        private final ITaskRunner task;

        private SafeTaskRunnable(ITaskRunner task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.execute();
            } catch (Throwable e) {
                LOG.error("task encountered error {}", e.getMessage(), e);
            }
        }

    }
}
