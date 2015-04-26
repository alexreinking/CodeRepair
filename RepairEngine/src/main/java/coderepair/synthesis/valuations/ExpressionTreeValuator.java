package coderepair.synthesis.valuations;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.*;

public abstract class ExpressionTreeValuator extends ExpressionTreeVisitor<Double> {
    final SynthesisGraph synthesisGraph;

    ExpressionTreeValuator(SynthesisGraph synthesisGraph) {
        this.synthesisGraph = synthesisGraph;
    }

    public final double assess(ExpressionTree tree) {
        Double value = visit(tree);
        if (value == null)
            throw new RuntimeException("Tree could not be fully valuated");
        return value;
    }

    @Override
    public final Double visit(ExpressionTree tree) {
        if (tree.getCost() == null)
            tree.setCost(super.visit(tree));
        return tree.getCost();
    }

    @Override
    public abstract Double visitConstructor(ConstructorExpressionTree tree);

    @Override
    public abstract Double visitMethodCall(MethodCallExpressionTree tree);

    @Override
    public abstract Double visitValue(ValueExpressionTree tree);

    @Override
    public abstract Double visitFieldAccess(FieldAccessExpressionTree tree);

    @Override
    public abstract Double visitClassCast(ClassCastExpressionTree tree);

    @Override
    public abstract Double visitStaticMethodCall(StaticMethodCallExpressionTree tree);
}
