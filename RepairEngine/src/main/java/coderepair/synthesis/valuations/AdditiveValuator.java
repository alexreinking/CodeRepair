package coderepair.synthesis.valuations;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.*;

import java.util.HashSet;
import java.util.List;

/**
 * Created by alexreinking on 3/10/15.
 */
public class AdditiveValuator extends ExpressionTreeValuator {
    public AdditiveValuator(SynthesisGraph synthesisGraph) {
        super(synthesisGraph);
    }

    private int countDuplicates(List<ExpressionTree> args) {
        return args.size() - new HashSet<>(args).size();
    }

    @Override
    public Double visitClassCast(ClassCastExpressionTree tree) {
        return nodeValue(tree);
    }

    private double nodeValue(ExpressionTree tree) {
        JavaFunctionNode leaf = tree.getLeaf();
        JavaTypeNode output = leaf.getOutput();
        double leafWeight = synthesisGraph.getWeight(output, leaf);
        return (leafWeight + sumChildren(tree)) * (1 + countDuplicates(tree.getChildren()));
    }

    private double sumChildren(ExpressionTree tree) {
        double sum = 0.0;
        for (ExpressionTree t : tree.getChildren()) sum += assess(t);
        return sum;
    }

    @Override
    public Double visitConstructor(ConstructorExpressionTree tree) {
        return nodeValue(tree);
    }

    @Override
    public Double visitFieldAccess(FieldAccessExpressionTree tree) {
        return nodeValue(tree);
    }

    @Override
    public Double visitMethodCall(MethodCallExpressionTree tree) {
        return assess(tree.getInstance()) + nodeValue(tree);
    }

    @Override
    public Double visitStaticMethodCall(StaticMethodCallExpressionTree tree) {
        return nodeValue(tree);
    }

    @Override
    public Double visitValue(ValueExpressionTree tree) {
        return nodeValue(tree);
    }
}
