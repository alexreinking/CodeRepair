package coderepair.plugin;

import coderepair.graph.SynthesisGraph;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Optional;

class RepairInitiator extends TreePathScanner<Object, Void> {
    private final Trees trees;
    private final SynthesisGraph graph;

    RepairInitiator(JavacTask task, SynthesisGraph graph) {
        trees = Trees.instance(task);
        this.graph = graph;
    }

    @Override
    public Object visitVariable(VariableTree node, Void aVoid) {
        ExpressionTree rhsTree = node.getInitializer();
        Tree lhsTypeTree = node.getType();

        getTypeMirror(rhsTree).ifPresent(rhsType ->
                getTypeMirror(lhsTypeTree).ifPresent(lhsType -> {
                    if (TypeKind.ERROR.equals(rhsType.getKind()) && TypeKind.DECLARED.equals(lhsType.getKind())) {
                        if (graph.hasType(lhsType.toString())) {
                            System.out.println("\nWinston: info: Error detected! Attempting to repair...");

                            HashSet<TypedSnippet> snips = new HashSet<>();
                            for (double c = 0.9; c >= 0.4; c -= 0.1) {
                                TypedSnippet repairResult = new RepairScanner(trees, getCurrentPath(), c, graph).scan(rhsTree, null);
                                if (repairResult != null && !snips.contains(repairResult)) {
                                    snips.add(repairResult);
                                    if (snips.size() == 1)
                                        System.out.println("Winston: info: try one of these:");
                                    System.out.printf("    [%.2f] %s %s = %s;%n",
                                            c,
                                            lhsTypeTree,
                                            node.getName(),
                                            repairResult);
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
