package coderepair.plugin;

import coderepair.SynthesisGraph;
import coderepair.util.GraphLoader;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.*;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Optional;

public class RepairMessage implements Plugin {

    private final String serializedFile = "/Users/alexreinking/Development/CodeRepair/resources/graph.ser";
    private final String dataFile = "/Users/alexreinking/Development/CodeRepair/resources/rt.javap";
    private final SynthesisGraph graph = GraphLoader.getGraph(serializedFile, dataFile);

    private final String outputSeparator = "--------------------------------------------------";

    @Override
    public String getName() {
        return "coderepair.plugin.RepairMessage";
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
        private final Types types;

        public TypeIdentifier(JavacTask task) {
            trees = Trees.instance(task);
            types = task.getTypes();
        }

        private Optional<TypeMirror> getTypeMirror(Tree tree) {
            return Optional.ofNullable(tree).map((Tree t) -> trees.getTypeMirror(new TreePath(getCurrentPath(), tree)));
        }

        @Override
        public Object visitVariable(VariableTree node, Void aVoid) {
            Optional<ExpressionTree> rhsTree = Optional.ofNullable(node.getInitializer());
            Optional<Tree> lhsTypeTree = Optional.ofNullable(node.getType());

            rhsTree.flatMap(this::getTypeMirror).ifPresent((TypeMirror rhsType) ->
                    lhsTypeTree.flatMap(this::getTypeMirror).ifPresent((TypeMirror lhsType) -> {
                        if (TypeKind.ERROR.equals(rhsType.getKind()) && TypeKind.DECLARED.equals(lhsType.getKind())) {
                            if (graph.hasType(lhsType.toString())) {
                                System.out.println("Correctable error detected. Initiating repair...");
                                System.out.printf("Desired type: %s%n", lhsType);
                                System.out.printf("Expression: %s%n", rhsTree.get());
                                System.out.printf("Variable name: %s%n", node.getName());
                                System.out.println("\n" + outputSeparator);
                            }
                        }
                    }));

            return super.visitVariable(node, aVoid);
        }
    }
}
