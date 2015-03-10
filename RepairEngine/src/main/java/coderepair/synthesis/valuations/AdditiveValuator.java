package coderepair.synthesis.valuations;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.*;

/**
 * Created by alexreinking on 3/10/15.
 */
public class AdditiveValuator extends ExpressionTreeValuator {
    public AdditiveValuator(SynthesisGraph synthesisGraph) {
        super(synthesisGraph);
    }

    private double sumChildren(ExpressionTree tree) {
        double sum = 0.0;
        for (ExpressionTree t : tree.getChildren()) sum += valuate(t);
        return sum;
    }

    private double nodeValue(ExpressionTree tree) {
        JavaFunctionNode leaf = tree.getLeaf();
        JavaTypeNode output = leaf.getOutput();
        double leafWeight = synthesisGraph.getWeight(output, leaf);
        return leafWeight + sumChildren(tree);
    }

    @Override
    public Double visitClassCast(ClassCastExpressionTree tree) {
        return nodeValue(tree);
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
        double instanceCost = 0.0;
        if (tree.getInstance() != null)
            instanceCost = valuate(tree.getInstance());
        return instanceCost + nodeValue(tree);
    }

    @Override
    public Double visitValue(ValueExpressionTree tree) {
        return nodeValue(tree);
    }
}
