package coderepair.synthesis.valuations;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by alexreinking on 3/14/15.
 */
public class RepairValuator extends ExpressionTreeValuator {
    private final HashSet<JavaFunctionNode> enforced;

    public RepairValuator(SynthesisGraph synthesisGraph) {
        this(synthesisGraph, Collections.emptyList());
    }

    public RepairValuator(SynthesisGraph synthesisGraph, Collection<JavaFunctionNode> enforced) {
        super(synthesisGraph);
        this.enforced = new HashSet<>(enforced);
    }

    private TraversalResult traverseSubtrees(ExpressionTree exp) {
        TraversalResult result = new TraversalResult(
                synthesisGraph.getWeight(exp.getLeaf().getOutput(), exp.getLeaf()),
                new HashSet<>());

        result.children.add(exp.getLeaf());
        for (ExpressionTree tree : exp.getChildren()) {
            TraversalResult subtreeResult = traverseSubtrees(tree);
            result.children.addAll(subtreeResult.children);
            result.baseCost += subtreeResult.baseCost;
        }

        return result;
    }

    public void strongEnforce(ExpressionTree toEnforce) {
        strongEnforce(toEnforce.getLeaf().getOutput().getName(), toEnforce.asExpression());
    }

    public void strongEnforce(String typeName, String expression) {
        System.out.printf("Enforced: %s%n", expression);
        JavaTypeNode typeNode = synthesisGraph.getTypeByName(typeName);

        JavaFunctionNode fn = synthesisGraph.getNodeManager().makeValue(expression, typeNode.getName());
        synthesisGraph.addVertex(fn);
        synthesisGraph.setEdgeWeight(synthesisGraph.addEdge(typeNode, fn), 0.001);
        enforced.add(fn);
    }

    public void relax() {
        synthesisGraph.removeAllVertices(enforced);
        enforced.clear();
    }

    @Override
    public Double visitConstructor(ConstructorExpressionTree tree) {
        return nodeValue(tree);
    }

    private double nodeValue(ExpressionTree tree) {
        TraversalResult result = traverseSubtrees(tree);
        double duplicatePenalty = 1 + countDuplicates(result.children);
        double enforcedBonus = 1 << countEnforced(result);
        return result.baseCost * duplicatePenalty / enforcedBonus;
    }

    private int countDuplicates(Collection<JavaFunctionNode> args) {
        return args.size() - new HashSet<>(args).size();
    }

    private int countEnforced(TraversalResult result) {
        result.children.retainAll(enforced);
        return result.children.size();
    }

    @Override
    public Double visitMethodCall(MethodCallExpressionTree tree) {
        double baseValue = assess(tree.getInstance()) + nodeValue(tree);
        if (enforced.contains(tree.getInstance().getLeaf()))
            baseValue /= 2.0;
        return baseValue;
    }

    @Override
    public Double visitValue(ValueExpressionTree tree) {
        return nodeValue(tree);
    }

    @Override
    public Double visitFieldAccess(FieldAccessExpressionTree tree) {
        return nodeValue(tree);
    }

    @Override
    public Double visitClassCast(ClassCastExpressionTree tree) {
        return nodeValue(tree);
    }

    @Override
    public Double visitStaticMethodCall(StaticMethodCallExpressionTree tree) {
        return nodeValue(tree);
    }

    private class TraversalResult {
        public HashSet<JavaFunctionNode> children;
        public double baseCost;

        public TraversalResult(double baseCost, HashSet<JavaFunctionNode> children) {
            this.baseCost = baseCost;
            this.children = children;
        }
    }
}
