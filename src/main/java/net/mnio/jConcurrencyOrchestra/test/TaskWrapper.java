package net.mnio.jConcurrencyOrchestra.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;


/**
 * Wrapper to be extended for running arbitrary code within {@link OrchestratedInterruptServiceImpl} in its own thread.
 */
public abstract class TaskWrapper extends Thread implements Task {

    private List<String> exceptionMessages = new ArrayList<>();

    private List<Class<? extends Throwable>> exceptionTypes = new ArrayList<>();

    private boolean success;

    private boolean isWaiting;

    /**
     * Creates thread with given name as suffix
     *
     * @param name
     */
    public TaskWrapper(final String name) {
        setName(getName() + "-Task-" + name);
    }


    /**
     * Creates thread with random name as suffix
     */
    public TaskWrapper() {
        this(UUID.randomUUID().toString().split("-")[0]);
    }

    /**
     * Starts and waits immediately until notified to execute {@link #toBeCalled()}.
     * Records status and exceptions.
     */
    @Override
    public synchronized void run() {
        try {
            doWait();
        } catch (final InterruptedException ignore) {
        }

        try {
            toBeCalled();
            success = true;
        } catch (final Throwable e) {
            collectExceptionDataRecursively(e);
            e.printStackTrace();
        }
    }

    /**
     * True if message is found in any exception message thrown.
     * It will search recursively within stack trace.
     *
     * @param messageContains
     * @return
     */
    public boolean hasExceptionMessage(final String messageContains) {
        for (final String exceptionMessage : exceptionMessages) {
            if (exceptionMessage.contains(messageContains)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if exception type is matching any exception thrown.
     * It will search recursively within stack trace.
     *
     * @param typeLookingFor
     * @return
     */
    public boolean hasExceptionType(final Class<? extends Throwable> typeLookingFor) {
        for (final Class<? extends Throwable> exceptionType : exceptionTypes) {
            if (exceptionType.equals(typeLookingFor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if no exception occurred.
     *
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return format("%s is waiting: %s", getName(), isWaiting);
    }

    /**
     * {@inheritDoc}
     *
     * @throws Throwable
     */
    public abstract void toBeCalled() throws Throwable;

    synchronized void doWait() throws InterruptedException {
        isWaiting = true;
        wait();
    }

    synchronized void doNotify() {
        isWaiting = false;
        notifyAll();
    }

    synchronized boolean isWaiting() {
        return isWaiting;
    }

    private void collectExceptionDataRecursively(Throwable t) {
        while (t != null) {
            exceptionMessages.add(t.getMessage());
            exceptionTypes.add(t.getClass());
            t = t.getCause();
        }
    }
}
