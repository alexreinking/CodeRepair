package coderepair.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class TimedTask {
    private final String taskName;
    private final Runnable task;
    private final ArrayList<Long> times = new ArrayList<>();
    private int nTrials = 1;

    public TimedTask(String taskName, Runnable task) {
        this.taskName = taskName;
        this.task = task;
    }

    public TimedTask times(int nTrials) {
        times.clear();
        times.ensureCapacity(nTrials);
        this.nTrials = nTrials;
        return this;
    }

    public TimedTask andThen(final TimedTask next) {
        final TimedTask self = this;
        return new TimedTask("", () -> {
            try {
                self.run();
                next.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void run() {
        for (int i = 0; i < nTrials; i++) {
            long startTime = System.currentTimeMillis();
            task.run();
            long stopTime = System.currentTimeMillis();
            times.add(stopTime - startTime);
        }
        Collections.sort(times);
        try {
            if (!taskName.isEmpty()) {
                PrintWriter writer = new PrintWriter(taskName + ".txt", StandardCharsets.UTF_8);
                times.forEach(writer::println);
                writer.close();
                System.out.printf("%s took %dms (best of %d)\n", taskName, times.get(0), nTrials);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TimedTask orElse(final TimedTask next) {
        final TimedTask self = this;
        return new TimedTask("", () -> {
            try {
                self.run();
                return;
            } catch (Exception e) {
                System.err.printf("%s failed. Reason: %s\n", self.taskName, e.getMessage());
            }
            try {
                next.run();
            } catch (Exception e) {
                System.err.printf("%s failed. Reason: %s\n", next.taskName, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}
