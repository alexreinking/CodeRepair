package coderepair.util;

/**
* Created by alexreinking on 10/6/14.
*/
public class TimedTask {
    private final String taskName;
    private final Runnable task;

    public TimedTask(String taskName, Runnable task) {
        this.taskName = taskName;
        this.task = task;
    }

    public void run() {
        run(1);
    }

    public void run(int nTrials) {
        long bestTime = Long.MAX_VALUE;
        for (int i = 0; i < nTrials; i++) {
            long startTime = System.currentTimeMillis();
            task.run();
            long stopTime = System.currentTimeMillis();
            if (stopTime - startTime < bestTime)
                bestTime = stopTime - startTime;
        }
        System.out.printf("%s took %dms (best of %d)\n", taskName, bestTime, nTrials);
    }
}
