package net.mnio.jOrchestra;

public interface InterruptService {

    default void interrupt() {
    }

    default void interrupt(final String description) {
    }
}
