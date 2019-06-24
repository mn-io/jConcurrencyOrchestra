package net.mnio.jOrchestra.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TaskSchedule {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final Task[] runningOrder;

    /**
     * Contains all tasks in given running order but without duplicates.
     * List instead of Set is used here to preserve its order provided by running order.
     */
    private final List<Task> allWithoutDuplicates;

    TaskSchedule(final Task... runningOrder) {
        if (runningOrder == null) {
            this.runningOrder = new Task[0];
        } else {
            this.runningOrder = runningOrder;
        }

        this.allWithoutDuplicates = listAllWithoutDuplicates(this.runningOrder);
    }

    private List<Task> listAllWithoutDuplicates(final Task[] runningOrder) {
        final List<Task> tasks = new ArrayList<>();
        for (final Task task : runningOrder) {
            if (!tasks.contains(task)) {
                tasks.add(task);
            }
        }
        return Collections.unmodifiableList(tasks);
    }

    Task getByOrder(final int i) {
        return runningOrder[i];
    }

    int getOrderLength() {
        return runningOrder.length;
    }

    List<Task> getAll() {
        return allWithoutDuplicates;
    }

    void logRunningOrder() {
        final String asString = Arrays.stream(runningOrder)
                .map(Thread::getName)
                .collect(Collectors.joining(", "));
        log.info("Running order: " + asString);
    }
}
