package net.mnio.jConcurrencyOrchestra.test;

import net.mnio.jConcurrencyOrchestra.InterruptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a simple use case (service), which could be tested on how it behaves on interruptions or race conditions.
 */
class MyService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final InterruptService interruptService;

    private final List<String> results;

    /**
     * @param interruptService InterruptService is needed in order to interrupt according to scheduled tasks.
     *                         Depending on underlying framework this could be accessed by dependency injection, etc.
     */
    MyService(final InterruptService interruptService) {
        this.interruptService = interruptService;
        this.results = new ArrayList<>();
    }

    void doSomething() {
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        // e.g. "Thread-0-Task-/1 => 1" for Task with name /1
        // NB: Keep in mind that Thread.currentThread().getName() is returning the task name - task extends threads and we call `setName()`
        final String one = Thread.currentThread().getName() + " => 1";
        log.info(one);
        results.add(one);

        // before stopping here it will print "1. time for thread - Interruption '1->2' called"
        interruptService.interrupt("1->2");
        // before continue here it will print "1. time for thread - Continue from interruption '1->2'"

        // e.g. "Thread-0-Task-/1 => 2" for Task with name /1
        final String two = Thread.currentThread().getName() + " => 2";
        log.info(two);
        results.add(two);

        // before stopping here it will print "2. time for thread - Interruption '2->3' called"
        interruptService.interrupt("2->3");
        // before continue here it will print "2. time for thread - Continue from interruption '2->3'"

        // e.g. "Thread-0-Task-/1 => 3" for Task with name /1
        final String three = Thread.currentThread().getName() + " => 3";
        log.info(three);
        results.add(three);

        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    List<String> getResults() {
        return results;
    }
}
