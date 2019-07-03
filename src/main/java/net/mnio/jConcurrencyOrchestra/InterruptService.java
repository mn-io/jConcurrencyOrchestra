package net.mnio.jConcurrencyOrchestra;

public interface InterruptService {

    /**
     * If called it checks whether the current thread is a task and start waiting.
     * Before and after interruption a log message is printed.
     */
    default void interrupt() {
        interrupt(null);
    }

    /**
     * If called it checks whether the current thread is a task and start waiting.
     * Before and after interruption a log message is printed.
     *
     * @param description Helpful hint to distinguish log messages before and after interruption.
     */
    default void interrupt(final String description) {
    }
}
