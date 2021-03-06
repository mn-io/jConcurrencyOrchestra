package net.mnio.jConcurrencyOrchestra.test;

class Ticker extends Thread {

    private static final int TICK_TIMEOUT = 200;

    private final TaskSchedule schedule;

    private int currentTaskNumber = -1;

    Ticker(final TaskSchedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                if (hasWaitingThreads()) {
                    notifyNext();
                }

                Thread.sleep(TICK_TIMEOUT);
            }
        } catch (final InterruptedException ignore) {
        }
    }

    private boolean hasWaitingThreads() {
        for (final TaskWrapper task : schedule.getAll()) {
            final State state = task.getState();
            if (state == State.TERMINATED) {
                continue;
            }
            if (!task.isWaiting()) {
                return false;
            }
        }

        return true;
    }

    private void notifyNext() {
        currentTaskNumber++;

        final TaskWrapper nextTask;
        if (currentTaskNumber >= schedule.getOrderLength()) {
            nextTask = findFirstWaitingThread();
        } else {
            nextTask = schedule.getByOrder(currentTaskNumber);
        }

        if (nextTask != null) {
            nextTask.doNotify();
        }
    }

    private TaskWrapper findFirstWaitingThread() {
        for (final TaskWrapper task : schedule.getAll()) {
            if (task.isWaiting()) {
                return task;
            }
        }
        return null;
    }
}
