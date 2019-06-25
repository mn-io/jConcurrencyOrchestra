package net.mnio.jOrchestra;

public interface InterruptService {

    default void interrupt() {
        interrupt(null);
    }

    default void interrupt(final String description) {
    }
}
