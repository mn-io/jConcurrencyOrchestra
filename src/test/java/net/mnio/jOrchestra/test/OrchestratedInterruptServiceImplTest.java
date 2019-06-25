package net.mnio.jOrchestra.test;

import net.mnio.jOrchestra.InterruptService;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static net.mnio.jOrchestra.test.OrchestratedInterruptServiceImplTest.MyThrowExceptionTask.MSG;
import static org.junit.Assert.*;


public class OrchestratedInterruptServiceImplTest {

    @Test
    public void testIntegration() throws Exception {
        final OrchestratedInterruptServiceImpl interruptService = new OrchestratedInterruptServiceImpl();
        final MyService service = new MyService(interruptService);
        final Task task1 = new MyServiceExecutorTask(service);
        final Task task2 = new MyServiceExecutorTask(service);

        final boolean success = interruptService.start(task1, task1, task2);
        assertTrue(success);

        final List<Integer> results = service.getResults();
        assertEquals(1, results.get(0).intValue());
        assertEquals(2, results.get(1).intValue());
        assertEquals(1, results.get(2).intValue());
        assertEquals(3, results.get(3).intValue());
        assertEquals(2, results.get(4).intValue());
        assertEquals(3, results.get(5).intValue());
    }

    @Test
    public void testExceptionHandling() throws Exception {
        final OrchestratedInterruptServiceImpl interruptService = new OrchestratedInterruptServiceImpl();

        final MyService service = new MyService(interruptService);
        final Task task1 = new MyServiceExecutorTask(service);
        final Task task2 = new MyThrowExceptionTask();

        interruptService.start(task1, task2, task1);

        assertTrue(task1.isSuccess());
        assertFalse(task2.isSuccess());
        assertTrue(task2.hasExceptionMessage(MSG));
    }

    @Test
    public void testDoNotInterrupt() throws Exception {
        final InterruptService interruptService = new InterruptService() {
        };

        final MyService service = new MyService(interruptService);

        service.doSomething();
        final List<Integer> actualResults = service.getResults();
        final List<Integer> expectedResults = Arrays.asList(1, 2, 3);
        assertEquals(expectedResults, actualResults);
    }

    /**
     * Executes job given by external service in our task environment.
     */
    static class MyServiceExecutorTask extends Task {

        private final MyService service;

        MyServiceExecutorTask(final MyService service) {
            this.service = service;
        }

        @Override
        protected void toBeCalled() {
            service.doSomething();
        }
    }

    /**
     * Does nothing beside throwing an exception to test what happens in case.
     */
    static class MyThrowExceptionTask extends Task {

        static final String MSG = "This exception is expected";

        @Override
        protected void toBeCalled() {
            throw new RuntimeException(MSG);
        }
    }
}
