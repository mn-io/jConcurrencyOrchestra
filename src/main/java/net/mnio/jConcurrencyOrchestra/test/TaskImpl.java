package net.mnio.jConcurrencyOrchestra.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;


/**
 * Wrapper to be extended for running arbitrary code within {@link OrchestratedInterruptServiceImpl}.
 */
public abstract class TaskImpl extends Thread implements Task {

    private List<String> exceptionMessages = new ArrayList<>();

    private List<Class<? extends Throwable>> exceptionTypes = new ArrayList<>();

    private boolean success;

    private boolean isWaiting;

    public TaskImpl(final String name) {
        setName(getName() + "-TaskImpl-" + name);
    }

    public TaskImpl() {
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

    public boolean hasExceptionMessage(final String s) {
        for (final String exceptionMessage : exceptionMessages) {
            if (exceptionMessage.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasExceptionType(final Class<? extends Throwable> t) {
        for (final Class<? extends Throwable> exceptionType : exceptionTypes) {
            if (exceptionType.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return format("%s is waiting: %s", getName(), isWaiting);
    }

    /**
     * Invoke your code to be be executed here.
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
