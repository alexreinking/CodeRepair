package coderepair.synthesis;

import coderepair.graph.JavaFunctionNode;
import coderepair.synthesis.trees.*;
import coderepair.synthesis.valuations.ExpressionTreeValuator;

import java.util.Arrays;
import java.util.Comparator;

public class ExpressionTreeBuilder {
    private final ExpressionTreeValuator valuator;

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
                expr = new ConstructorExpressionTree(fn, args.clone()); // TODO: every day remind myself that Java has pointers
                break;
            case Method:
                ExpressionTree instance = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                expr = new MethodCallExpressionTree(fn, args, instance);
                break;
            case StaticMethod:
                expr = new StaticMethodCallExpressionTree(fn, args.clone());
                break;
            case Field:
                if (args.length != 1)
                    throw new IllegalArgumentException("Fields must at least have an owner, and cannot take arguments.");
                expr = new FieldAccessExpressionTree(fn, args[0]);
                break;
            case StaticField: // unused
                break;
            case Value:
                if (args.length != 0)
                    throw new IllegalArgumentException("Values may not take arguments.");
                expr = new ValueExpressionTree(fn);
                break;
            case Type: // unused
                break;
        }
        valuator.assess(expr);
        return expr;
    }

    public Comparator<ExpressionTree> getComparator() {
        return (o1, o2) -> {
            int diff = Double.compare(valuator.assess(o1), valuator.assess(o2));
            return (diff != 0) ? diff : o1.asExpression().compareTo(o2.asExpression());
        };
    }
}
