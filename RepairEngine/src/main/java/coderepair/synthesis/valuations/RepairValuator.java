package coderepair.synthesis.valuations;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.*;

/**
 * Created by alexreinking on 3/14/15.
 */
public class RepairValuator extends ExpressionTreeValuator {
    public RepairValuator(SynthesisGraph synthesisGraph) {
        super(synthesisGraph);
    }

    @Override
    public Double visitConstructor(ConstructorExpressionTree tree) {
        return null;
    }

    @Override
    public Double visitMethodCall(MethodCallExpressionTree tree) {
        return null;
    }

    @Override
    public Double visitValue(ValueExpressionTree tree) {
        return null;
    }

    @Override
    public Double visitFieldAccess(FieldAccessExpressionTree tree) {
        return null;
    }

    @Override
    public Double visitClassCast(ClassCastExpressionTree tree) {
        return null;
    }

    @Override
    public Double visitStaticMethodCall(StaticMethodCallExpressionTree tree) {
        return null;
    }
}
