package net.mnio.jConcurrenyOrchestra;

public interface InterruptService {

    default void interrupt() {
        interrupt(null);
    }

    default void interrupt(final String description) {
    }
}
