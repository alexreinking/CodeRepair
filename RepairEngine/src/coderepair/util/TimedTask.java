package coderepair.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by alexreinking on 10/6/14.
 */
public class TimedTask {
    private final String taskName;
    private final Runnable task;
    private final ArrayList<Long> times = new ArrayList<Long>();

    public TimedTask(String taskName, Runnable task) {
        this.taskName = taskName;
        this.task = task;
    }

    public void run() {
        run(1);
    }

    public void run(int nTrials) {
        times.clear();
        times.ensureCapacity(nTrials);
        for (int i = 0; i < nTrials; i++) {
            long startTime = System.currentTimeMillis();
            task.run();
            long stopTime = System.currentTimeMillis();
            times.add(stopTime - startTime);
        }
        Collections.sort(times);
        try {
            if (!taskName.isEmpty()) {
                PrintWriter writer = new PrintWriter(taskName + ".txt", "UTF-8");
                for (Long time : times) writer.println(time);
                writer.close();
                System.out.printf("%s took %dms (best of %d)\n", taskName, times.get(0), nTrials);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public TimedTask andThen(final TimedTask next) {
        final TimedTask self = this;
        return new TimedTask("", new Runnable() {
            @Override
            public void run() {
                try {
                    self.run();
                    next.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public TimedTask orElse(final TimedTask next) {
        final TimedTask self = this;
        return new TimedTask("", new Runnable() {
            @Override
            public void run() {
                try {
                    self.run();
                    return;
                } catch (Exception e) {
                    System.err.printf("%s failed. Reason: %s\n", self.taskName, e.getMessage());
                    e.printStackTrace();
                }
                try {
                    next.run();
                } catch (Exception e) {
                    System.err.printf("%s failed. Reason: %s\n", next.taskName, e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
