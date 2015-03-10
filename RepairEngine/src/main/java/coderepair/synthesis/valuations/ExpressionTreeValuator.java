package coderepair.synthesis.valuations;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.ExpressionTree;
import coderepair.synthesis.trees.ExpressionTreeVisitor;

/**
 * Created by alexreinking on 3/10/15.
 */
public class ExpressionTreeValuator extends ExpressionTreeVisitor<Double> {
    protected final SynthesisGraph synthesisGraph;

    public ExpressionTreeValuator(SynthesisGraph synthesisGraph) {
        this.synthesisGraph = synthesisGraph;
    }

    public final double valuate(ExpressionTree tree) {
        Double value = visit(tree);
        if (value == null)
            throw new RuntimeException("Tree could not be fully valuated");
        return value;
    }

    @Override
    public final Double visit(ExpressionTree tree) {
        if (tree.getCost() != null)
            return tree.getCost();

        Double value = super.visit(tree);
        if (value != null)
            tree.setCost(value);
        return value;
    }
}
