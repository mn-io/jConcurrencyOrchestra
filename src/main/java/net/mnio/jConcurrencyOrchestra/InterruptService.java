package net.mnio.jConcurrencyOrchestra;

public interface InterruptService {

    default void interrupt() {
        interrupt(null);
    }

    default void interrupt(final String description) {
    }
}
