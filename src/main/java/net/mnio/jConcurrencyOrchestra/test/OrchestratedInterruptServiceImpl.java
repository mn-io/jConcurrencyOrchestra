package net.mnio.jConcurrencyOrchestra.test;

import net.mnio.jConcurrencyOrchestra.InterruptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

public class OrchestratedInterruptServiceImpl implements InterruptService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private ThreadLocal<AtomicInteger> interruptCounter = new ThreadLocal<>();

    private TaskSchedule schedule;

    /**
     * executes all tasks in given order.
     * If last task is executed and interruptions are still called, it will ignore and continue with finishing them in same running order given.
     * e.g. task1, then task2, and so on.
     *
     * @param runningOrder
     * @return true if all tasks ran successfully
     * @throws InterruptedException
     */
    public boolean start(final TaskImpl... runningOrder) throws InterruptedException {
        schedule = new TaskSchedule(runningOrder);
        schedule.logRunningOrder();

        final Ticker ticker = new Ticker(schedule);

        // tasks invoke doWait() at beginning of run() to be on hold...
        for (final TaskImpl task : schedule.getAll()) {
            task.start();
        }

        // ... and now continue and interrupt in given order until running order is empty, so ...
        ticker.start();

        // we can wait infinite until all tasks are done ...
        for (final TaskImpl task : schedule.getAll()) {
            task.join();
        }

        // and stop the ticker as all tasks are done
        ticker.interrupt();

        // collect results
        boolean result = true;
        for (final TaskImpl task : schedule.getAll()) {
            if (!task.isSuccess()) {
                result = false;
                break;
            }
        }

        schedule = null;
        return result;
    }

    public boolean start(final Task... runningOrder) throws InterruptedException {
        final HashMap<Task, TaskImpl> taskMapper = new HashMap<>();
        final TaskImpl[] wrapped = new TaskImpl[runningOrder.length];
        for (int i = 0; i < runningOrder.length; i++) {
            final Task task = runningOrder[i];

            if (taskMapper.containsKey(task)) {
                wrapped[i] = taskMapper.get(task);
            } else {
                final TaskImpl taskImpl = new TaskImpl() {
                    @Override
                    public void toBeCalled() throws Throwable {
                        task.toBeCalled();
                    }
                };
                taskMapper.put(task, taskImpl);
                wrapped[i] = taskImpl;
            }
        }
        return start(wrapped);
    }

    /**
     * If called, it checks whether the current thread is a task and start waiting.
     *
     * @param description
     */
    @Override
    public void interrupt(final String description) {
        if (schedule == null) {
            throw new IllegalStateException("Cannot execute without start() call first.");
        }

        final String actualDescription;
        if (description == null) {
            actualDescription = "";
        } else {
            actualDescription = format(" '%s'", description);
        }

        try {
            final Thread currentThread = Thread.currentThread();
            for (final TaskImpl task : schedule.getAll()) {
                if (task.equals(currentThread)) {
                    synchronized (task) {
                        final int currentCount = getCount();
                        log.info(format("%d - Interruption%s called", currentCount, actualDescription));
                        task.doWait();
                        log.info(format("%d - Continue from interruption%s", currentCount, actualDescription));
                        return;
                    }
                }
            }

        } catch (final InterruptedException ignore) {
        }
    }

    private int getCount() {
        AtomicInteger counter = interruptCounter.get();
        if (counter == null) {
            counter = new AtomicInteger();
            interruptCounter.set(counter);
        }

        return counter.incrementAndGet();
    }
}
