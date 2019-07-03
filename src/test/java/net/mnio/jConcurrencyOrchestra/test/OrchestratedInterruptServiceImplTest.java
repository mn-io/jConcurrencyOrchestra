package net.mnio.jConcurrencyOrchestra.test;

import net.mnio.jConcurrencyOrchestra.InterruptService;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImplTest.MyThrowExceptionTask.MSG;
import static org.junit.Assert.*;

public class OrchestratedInterruptServiceImplTest {

    @Test
    public void happyPathJava7() throws Exception {
        // Hint: run this test and read its log output
        final OrchestratedInterruptServiceImpl interruptService = new OrchestratedInterruptServiceImpl();

        // First the OrchestratedInterruptService needs to be applied to our tested component, here MyService.
        // This is just a dummy service, which collects results [1,2,3] and is interrupted quite each time.
        final MyService service = new MyService(interruptService);

        // Second we need several threads (= tasks here) to be working with the same service.
        final TaskWrapper task1 = new MyServiceExecutorTask("/1", service);
        final TaskWrapper task2 = new MyServiceExecutorTask("/2", service);

        // start the tasks in given order. 
        // When task2 interrupts again it will finish task1 first as it is first in running order.
        final boolean success = interruptService.start(task1, task1, task2);

        // Only if every task run without exception, it will be true
        assertTrue(success);

        // And in here we verify our collected results. 
        // Each assert in a single line to be able to track irregularities more elegant.
        final List<String> results = service.getResults();
        assertTrue(results.get(0).endsWith("Task-/1 => 1")); // Task1 puts 1.
        assertTrue(results.get(1).endsWith("Task-/1 => 2")); // Task1 is in running order again and puts 2.
        assertTrue(results.get(2).endsWith("Task-/2 => 1")); // Now Task2 takes over and put another 1.
        assertTrue(results.get(3).endsWith("Task-/1 => 3")); // We finish by given running order from beginning, which is Task1
        assertTrue(results.get(4).endsWith("Task-/2 => 2")); // Task2 can now finish ...
        assertTrue(results.get(5).endsWith("Task-/2 => 3")); // ... until the end.
    }

    @Test
    public void happyPathJava8() throws Exception {
        final OrchestratedInterruptServiceImpl interruptService = new OrchestratedInterruptServiceImpl();

        final MyService service = new MyService(interruptService);
        final Task task1 = service::doSomething; // we can directly assign a function to be called
        final Task task2 = service::doSomething; // but it does not allow to assign a name to be better for understanding

        final boolean success = interruptService.start(task1, task1, task2);
        assertTrue(success);

        final List<String> results = service.getResults();
        assertTrue(results.get(0).endsWith(" => 1"));
        assertTrue(results.get(1).endsWith(" => 2"));
        assertTrue(results.get(2).endsWith(" => 1"));
        assertTrue(results.get(3).endsWith(" => 3"));
        assertTrue(results.get(4).endsWith(" => 2"));
        assertTrue(results.get(5).endsWith(" => 3"));
    }

    @Test
    public void handlesException() throws Exception {
        final OrchestratedInterruptServiceImpl interruptService = new OrchestratedInterruptServiceImpl();

        final MyService service = new MyService(interruptService);
        final TaskWrapper task1 = new MyServiceExecutorTask(null, service);
        final TaskWrapper task2 = new MyThrowExceptionTask();

        final boolean success = interruptService.start(task1, task2, task1);
        assertFalse(success);
        assertTrue(task1.isSuccess());
        assertFalse(task2.isSuccess());
        assertTrue(task2.hasExceptionMessage(MSG));
    }

    @Test
    public void doesNotInterrupt() throws Exception {
        final InterruptService interruptService = new InterruptService() {
        };

        final MyService service = new MyService(interruptService);

        // runs only on main thread as result indicates
        service.doSomething();
        final List<String> actualResults = service.getResults();
        final List<String> expectedResults = Arrays.asList("main => 1", "main => 2", "main => 3");
        assertEquals(expectedResults, actualResults);
    }

    /**
     * Executes job given by external service in our task environment.
     */
    static class MyServiceExecutorTask extends TaskWrapper {

        private final MyService service;

        MyServiceExecutorTask(final String name, final MyService service) {
            super(name);
            this.service = service;
        }

        @Override
        public void toBeCalled() {
            service.doSomething();
        }
    }

    /**
     * Does nothing beside throwing an exception to test what happens in case.
     */
    static class MyThrowExceptionTask extends TaskWrapper {

        static final String MSG = "This exception is expected";

        @Override
        public void toBeCalled() {
            throw new RuntimeException(MSG);
        }
    }
}
