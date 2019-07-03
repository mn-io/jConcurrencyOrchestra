package net.mnio.jConcurrenyOrchestra.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TaskSchedule {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final TaskImpl[] runningOrder;

    /**
     * Contains all tasks in given running order but without duplicates.
     * List instead of Set is used here to preserve its order provided by running order.
     */
    private final List<TaskImpl> allWithoutDuplicates;

    TaskSchedule(final TaskImpl... runningOrder) {
        if (runningOrder == null) {
            this.runningOrder = new TaskImpl[0];
        } else {
            this.runningOrder = runningOrder;
        }

        this.allWithoutDuplicates = listAllWithoutDuplicates(this.runningOrder);
    }

    private List<TaskImpl> listAllWithoutDuplicates(final TaskImpl[] runningOrder) {
        final List<TaskImpl> tasks = new ArrayList<>();
        for (final TaskImpl task : runningOrder) {
            if (!tasks.contains(task)) {
                tasks.add(task);
            }
        }
        return Collections.unmodifiableList(tasks);
    }

    TaskImpl getByOrder(final int i) {
        return runningOrder[i];
    }

    int getOrderLength() {
        return runningOrder.length;
    }

    List<TaskImpl> getAll() {
        return allWithoutDuplicates;
    }

    void logRunningOrder() {
        final String asString = Arrays.stream(runningOrder)
                .map(Thread::getName)
                .collect(Collectors.joining(", "));
        log.info("Running order: " + asString);
    }
}
