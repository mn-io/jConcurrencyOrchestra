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

        final String one = Thread.currentThread().getName() + " => 1";
        log.info(one);
        results.add(one);

        interruptService.interrupt();

        final String two = Thread.currentThread().getName() + " => 2";
        log.info(two);
        results.add(two);

        interruptService.interrupt();

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
