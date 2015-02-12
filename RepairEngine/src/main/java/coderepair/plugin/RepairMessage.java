package coderepair.plugin;

import coderepair.SynthesisGraph;
import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.CodeSynthesis;
import coderepair.util.GraphLoader;
import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

public class RepairMessage implements Plugin {

    private final String serializedFile = "/Users/alexreinking/Development/CodeRepair/resources/graph.ser";
    private final String dataFile = "/Users/alexreinking/Development/CodeRepair/resources/rt.javap";
    private final SynthesisGraph graph = GraphLoader.getGraph(serializedFile, dataFile);

    @Override
    public String getName() {
        return "coderepair.plugin.RepairMessage";
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {
        javacTask.addTaskListener(new TaskListener() {
            private final RepairInitiator repairInitiator = new RepairInitiator(javacTask);

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

    private class RepairInitiator extends TreePathScanner<Object, Void> {
        private final Trees trees;

        public RepairInitiator(JavacTask task) {
            trees = Trees.instance(task);
        }

        @Override
        public Object visitVariable(VariableTree node, Void aVoid) {
            ExpressionTree rhsTree = node.getInitializer();
            Tree lhsTypeTree = node.getType();

            getTypeMirror(rhsTree).ifPresent(rhsType ->
                    getTypeMirror(lhsTypeTree).ifPresent(lhsType -> {
                        if (TypeKind.ERROR.equals(rhsType.getKind()) && TypeKind.DECLARED.equals(lhsType.getKind())) {
                            if (graph.hasType(lhsType.toString())) {
                                System.out.println("Winston: info: Error detected! Attempting to repair...");
                                TypedSnippet scan = new RepairScanner(trees, getCurrentPath()).scan(rhsTree, null);
                                if (scan == null)
                                    System.out.println("Winston: warn: Sorry! No repairs found.");
                                else
                                    System.out.printf("Winston: This might work!%n \t%s %s = %s;%n",
                                            lhsTypeTree,
                                            node.getName(),
                                            scan);
                            }
                        }
                    }));

            return super.visitVariable(node, aVoid);
        }

        private Optional<TypeMirror> getTypeMirror(Tree tree) {
            return Optional.ofNullable(tree).map(t -> trees.getTypeMirror(new TreePath(getCurrentPath(), tree)));
        }
    }

    private class TypedSnippet {
        public String type;
        public String code;

        public TypedSnippet(String code, String type) {
            this.code = code;
            this.type = type;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    private class RepairScanner extends TreeScanner<TypedSnippet, Void> {
        private final Trees trees;
        CodeSynthesis synthesis = new CodeSynthesis(graph);
        private TreePath path;

        public RepairScanner(Trees trees, TreePath path) {
            this.trees = trees;
            this.path = path;
        }

        @Override
        public TypedSnippet visitNewClass(NewClassTree newClass, Void aVoid) {
            String className = getTypeMirror(newClass.getIdentifier()).map(TypeMirror::toString).get();

            return getTypeMirror(newClass).map(evaluatedType -> {
                if (TypeKind.ERROR.equals(evaluatedType.getKind())) {
                    newClass.getArguments().stream().forEach(arg -> {
                        String type = getTypeMirror(arg).map(TypeMirror::toString).get();
                        String code = arg.toString();
                        if (hasTypeError(arg)) {
                            TypedSnippet repaired = scan(arg, aVoid);
                            type = repaired.type;
                            code = repaired.code;
                        }
                        synthesis.strongEnforce(type, new CodeSnippet(code, 0.0001 * Math.random()));
                    });
                    return new TypedSnippet(synthesis.synthesize(className, 6.5, 5).first().code, className);
                } else {
                    return new TypedSnippet(newClass.toString(), className);
                }
            }).get();
        }

        private Optional<TypeMirror> getTypeMirror(Tree tree) {
            return Optional.ofNullable(tree).map(t -> trees.getTypeMirror(new TreePath(path, tree)));
        }

        private Boolean hasTypeError(Tree tree) {
            return getTypeMirror(tree).map(t -> TypeKind.ERROR.equals((TypeKind) t.getKind())).get();
        }

        @Override
        public TypedSnippet visitMethodInvocation(MethodInvocationTree node, Void aVoid) {
            System.out.printf("%s: %s%n", getTypeMirror(node).map(TypeMirror::toString).orElse("<no-type>"), node);
            System.out.printf("%s: %s: %s%n",
                    getTypeMirror(node.getMethodSelect()).map(TypeMirror::toString).orElse("<no-type>"),
                    node.getClass().getSimpleName(),
                    node.getMethodSelect());
            node.getArguments().stream().forEach(exp ->
                            System.out.printf("arg: %s: %s%n",
                                    getTypeMirror(exp).map(TypeMirror::toString).orElse("<no-type>"), exp)
            );
            return super.visitMethodInvocation(node, aVoid);
        }
    }
}
