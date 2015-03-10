package coderepair.synthesis;

import coderepair.graph.JavaFunctionNode;
import coderepair.synthesis.trees.*;
import coderepair.synthesis.valuations.ExpressionTreeValuator;

import java.util.Arrays;

public class ExpressionTreeBuilder {
    private ExpressionTreeValuator valuator;

    public ExpressionTreeBuilder(ExpressionTreeValuator valuator) {
        this.valuator = valuator;
    }

    public ExpressionTree buildInvocation(JavaFunctionNode fn, ExpressionTree[] args) {
        ExpressionTree expr = null;
        switch (fn.getKind()) {
            case ClassCast:
                if (args.length != 1)
                    throw new IllegalArgumentException("Error! Can only cast exactly one type to another");
                expr = new ClassCastExpressionTree(fn, args[0]);
                break;
            case Constructor:
                expr = new ConstructorExpressionTree(fn, args);
                break;
            case Method:
                ExpressionTree instance = null;
                if (!fn.isStatic()) {
                    instance = args[0];
                    args = Arrays.copyOfRange(args, 1, args.length);
                }
                expr = new MethodCallExpressionTree(fn, args, instance);
                break;
            case Value:
                if (fn.isStatic()) {
                    if (args.length != 1)
                        throw new IllegalArgumentException("Fields must at least have an owner, and cannot take arguments.");
                    expr = new FieldAccessExpressionTree(fn, args[0]);
                } else {
                    if (args.length != 0)
                        throw new IllegalArgumentException("Values may not take arguments.");
                    expr = new ValueExpressionTree(fn);
                }
                break;
        }
        valuator.valuate(expr);
        return expr;
    }
}
