package coderepair.plugin;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.CodeSynthesis;
import coderepair.synthesis.ExpressionTreeBuilder;
import coderepair.synthesis.trees.ExpressionTree;
import coderepair.synthesis.valuations.RepairValuator;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;
import java.util.SortedSet;

class RepairScanner extends TreeScanner<TypedSnippet, Void> {
    private final Trees trees;
    private final TreePath path;
    private final double conductanceTarget;
    private final SynthesisGraph graph;

    RepairScanner(Trees trees, TreePath path, double conductanceTarget, SynthesisGraph graph) {
        this.trees = trees;
        this.path = path;
        this.conductanceTarget = conductanceTarget;
        this.graph = graph;
    }

    @Override
    public TypedSnippet visitNewClass(NewClassTree newClass, Void aVoid) {
        String className = getTypeMirror(newClass.getIdentifier()).map(TypeMirror::toString).get();

        return getTypeMirror(newClass).map(evaluatedType -> {
            if (TypeKind.ERROR.equals(evaluatedType.getKind())) {
                RepairValuator repairValuator = new RepairValuator(graph);
                CodeSynthesis synthesis = new CodeSynthesis(graph, new ExpressionTreeBuilder(repairValuator));
                newClass.getArguments().forEach(arg -> {
                    String type = getTypeMirror(arg).map(TypeMirror::toString).get();
                    String code = arg.toString();
                    if (hasTypeError(arg)) {
                        TypedSnippet repaired = scan(arg, aVoid);
                        type = repaired.type;
                        code = repaired.code;
                    }
                    repairValuator.strongEnforce(type, code);
                });
                SortedSet<ExpressionTree> expressionTrees
                        = synthesis.synthesize(className, conductanceTarget, 5);
                repairValuator.relax();
                return new TypedSnippet(expressionTrees.first().asExpression(), className);
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

    @Override
    public TypedSnippet visitMethodInvocation(MethodInvocationTree node, Void aVoid) {
        System.out.printf("%s: %s%n", getTypeMirror(node).map(TypeMirror::toString).orElse("<no-type>"), node);
        System.out.printf("%s: %s: %s%n",
                getTypeMirror(node.getMethodSelect()).map(TypeMirror::toString).orElse("<no-type>"),
                node.getClass().getSimpleName(),
                node.getMethodSelect());
        node.getArguments().forEach(exp ->
                System.out.printf("arg: %s: %s%n",
                        getTypeMirror(exp).map(TypeMirror::toString).orElse("<no-type>"), exp)
        );
        return super.visitMethodInvocation(node, aVoid);
    }
}
