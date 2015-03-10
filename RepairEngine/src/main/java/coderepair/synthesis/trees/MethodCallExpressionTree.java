package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by alexreinking on 3/10/15.
 */
public class MethodCallExpressionTree extends ExpressionTree {
    private final ExpressionTree instance;
    private final ExpressionTree[] args;
    private final JavaFunctionNode method;

    public MethodCallExpressionTree(JavaFunctionNode method, ExpressionTree[] args, ExpressionTree instance) {
        this.args = args;
        this.instance = instance;
        this.method = method;
    }

    public ExpressionTree getInstance() {
        return instance;
    }

    @Override
    protected String collapse() {
        StringBuilder expr = new StringBuilder();
        if (instance != null)
            expr.append("(").append(instance.asExpression()).append(").");
        expr.append(method.getFunctionName()).append("(");
        StringJoiner argJoiner = new StringJoiner(", ");
        for (ExpressionTree arg : args) argJoiner.add(arg.asExpression());
        expr.append(argJoiner).append(")");
        return expr.toString();
    }

    @Override
    public List<ExpressionTree> getChildren() {
        return Arrays.asList(args);
    }

    @Override
    public JavaFunctionNode getLeaf() {
        return method;
    }
}
