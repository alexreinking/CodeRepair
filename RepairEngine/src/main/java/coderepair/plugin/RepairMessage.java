package coderepair.plugin;

import coderepair.graph.SynthesisGraph;
import coderepair.util.GraphLoader;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

public class RepairMessage implements Plugin {
    private static final String HOME_DIR = System.getProperty("user.home");
    private final String serializedFile = HOME_DIR + "/.winston/resources/graph.ser";
    private final String dataFile = HOME_DIR + "/.winston/resources/rt.javap";
    private final SynthesisGraph graph = GraphLoader.fromSerialized(serializedFile, dataFile);

    @Override
    public String getName() {
        return "coderepair.plugin.RepairMessage";
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {
        javacTask.addTaskListener(new TaskListener() {
            private final RepairInitiator repairInitiator = new RepairInitiator(javacTask, graph);

            @Override
            public void started(TaskEvent e) {
            }

            @Override
            public void finished(TaskEvent e) {
                if (e.getKind() == TaskEvent.Kind.ANALYZE)
                    repairInitiator.scan(e.getCompilationUnit(), null);
            }
        });
    }
}
