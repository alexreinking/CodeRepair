package coderepair.oracle;

import coderepair.SynthesisGraph;
import coderepair.util.GraphLoader;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.util.*;

import javax.lang.model.type.TypeMirror;

public class RepairMessage implements Plugin {

    private final String serializedFile = "/Users/alexreinking/Development/CodeRepair/resources/graph.ser";
    private final String dataFile = "/Users/alexreinking/Development/CodeRepair/resources/rt.javap";
    private final SynthesisGraph graph = GraphLoader.getGraph(serializedFile, dataFile);

    private final String outputSeparator = "--------------------------------------------------";

    @Override
    public String getName() {
        return "coderepair.sourcescanner.SourceStatistics";
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {
        javacTask.addTaskListener(new TaskListener() {
            private final TypeIdentifier typeIdentifier = new TypeIdentifier(javacTask);

            @Override
            public void started(TaskEvent e) {
            }

            @Override
            public void finished(TaskEvent e) {
                if (e.getKind() == TaskEvent.Kind.ANALYZE)
                    typeIdentifier.scan(e.getCompilationUnit(), null);
            }
        });
    }

    private class TypeIdentifier extends TreePathScanner<Object, Void> {
        private final Trees trees;

        public TypeIdentifier(JavacTask task) {
            trees = Trees.instance(task);
        }

        @Override
        public Object visitExpressionStatement(ExpressionStatementTree node, Void aVoid) {
            TypeMirror valueTypeMirror = trees.getTypeMirror(new TreePath(getCurrentPath(), node));

            System.out.printf("%s\n%n", outputSeparator);
            System.out.println(node.toString());
            System.out.println(valueTypeMirror.toString());
            System.out.printf("\n%s\n%n", outputSeparator);

            return super.visitExpressionStatement(node, aVoid);
        }
    }
}
