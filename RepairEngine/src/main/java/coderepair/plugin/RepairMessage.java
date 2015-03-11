package coderepair.plugin;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.CodeSynthesis;
import coderepair.synthesis.ExpressionTreeBuilder;
import coderepair.synthesis.valuations.AdditiveValuator;
import coderepair.util.GraphLoader;
import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Optional;

public class RepairMessage implements Plugin {

    private static final String HOME_DIR = System.getProperty("user.home");
    private final String serializedFile = HOME_DIR + "/.winston/resources/graph.ser";
    private final String dataFile = HOME_DIR + "/.winston/resources/rt.javap";
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

    private class TypedSnippet {
        public final String type;
        public final String code;

        public TypedSnippet(String code, String type) {
            this.code = code;
            this.type = type;
        }

        @Override
        public String toString() {
            return code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypedSnippet that = (TypedSnippet) o;

            return code.equals(that.code) && type.equals(that.type);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + code.hashCode();
            return result;
        }
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

                                HashSet<TypedSnippet> snips = new HashSet<>();
                                for (double c = 0.9; c >= 0.4; c -= 0.1) {
                                    TypedSnippet scan = new RepairScanner(trees, getCurrentPath(), c).scan(rhsTree, null);
                                    if (scan != null && !snips.contains(scan)) {
                                        snips.add(scan);
                                        if (snips.size() == 1)
                                            System.out.println("Winston: info: try one of these:");
                                        System.out.printf("\t[%2f] %s %s = %s;%n",
                                                c,
                                                lhsTypeTree,
                                                node.getName(),
                                                scan);
                                    }
                                }

                                if (snips.isEmpty())
                                    System.out.println("Winston: warn: Sorry! No repairs found.");
                            }
                        }
                    }));

            return super.visitVariable(node, aVoid);
        }

        private Optional<TypeMirror> getTypeMirror(Tree tree) {
            return Optional.ofNullable(tree).map(t -> trees.getTypeMirror(new TreePath(getCurrentPath(), tree)));
        }
    }

    private class RepairScanner extends TreeScanner<TypedSnippet, Void> {
        private final Trees trees;
        private final TreePath path;
        private final double conductanceTarget;

        public RepairScanner(Trees trees, TreePath path, double conductanceTarget) {
            this.trees = trees;
            this.path = path;
            this.conductanceTarget = conductanceTarget;
        }

        @Override
        public TypedSnippet visitNewClass(NewClassTree newClass, Void aVoid) {
            String className = getTypeMirror(newClass.getIdentifier()).map(TypeMirror::toString).get();

            return getTypeMirror(newClass).map(evaluatedType -> {
                if (TypeKind.ERROR.equals(evaluatedType.getKind())) {
                    CodeSynthesis synthesis = new CodeSynthesis(graph, new ExpressionTreeBuilder(new AdditiveValuator(graph)));
                    newClass.getArguments().stream().forEach(arg -> {
                        String type = getTypeMirror(arg).map(TypeMirror::toString).get();
                        String code = arg.toString();
                        if (hasTypeError(arg)) {
                            TypedSnippet repaired = scan(arg, aVoid);
                            type = repaired.type;
                            code = repaired.code;
                        }
//                        synthesis.strongEnforce(type, new CodeSnippet(code, 0.0001 * Math.random()));
                    });
                    return new TypedSnippet(synthesis.synthesize(className, conductanceTarget, 5).first().asExpression(), className);
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

        @Override
        public TypedSnippet scan(final Tree toRepair, Void aVoid) {
            TreePath methodPath = path, classPath = path;
            while (methodPath != null && methodPath.getLeaf().getKind() != Tree.Kind.METHOD)
                methodPath = methodPath.getParentPath();

            while (classPath != null && classPath.getLeaf().getKind() != Tree.Kind.CLASS)
                classPath = classPath.getParentPath();

            assert methodPath != null;
            assert classPath != null;

            new TreeScanner<Void, Void>() {
                private boolean found = false;

                @Override
                public Void visitVariable(VariableTree node, Void aVoid) {
                    if (toRepair.equals(node.getInitializer()))
                        found = true;
                    else if (!found) {
                        String type = getTypeMirror(node).map(TypeMirror::toString).orElse("<no-type>");
                        if (graph.hasType(type)) {
                            graph.addLocalVariable(node.getName().toString(), type);
                        } else {
                            System.err.println("Winston: warn: no data collected for type " + type);
                        }
                    }

                    return super.visitVariable(node, aVoid);
                }
            }.scan(methodPath, null);

            TypedSnippet scan = super.scan(toRepair, aVoid);
            graph.resetLocals();
            return scan;
        }
    }
}
