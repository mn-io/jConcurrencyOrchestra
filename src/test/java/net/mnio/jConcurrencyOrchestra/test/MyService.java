package net.mnio.jConcurrencyOrchestra.test;

import net.mnio.jConcurrencyOrchestra.InterruptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a simple use case (service), which could be tested how it behaves on interruptions or race conditions.
 */
class MyService {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final InterruptService interruptService;

    private final List<Integer> results;

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

        log.info(Thread.currentThread().getName() + " => 1");
        results.add(1);

        interruptService.interrupt();

        log.info(Thread.currentThread().getName() + " => 2");
        results.add(2);

        interruptService.interrupt();

        log.info(Thread.currentThread().getName() + " => 3");
        results.add(3);

        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    List<Integer> getResults() {
        return results;
    }
}
