package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by alexreinking on 3/10/15.
 */
public class StaticMethodCallExpressionTree extends ExpressionTree {
    private final ExpressionTree[] args;
    private final JavaFunctionNode method;

    public StaticMethodCallExpressionTree(JavaFunctionNode method, ExpressionTree[] args) {
        this.args = args;
        this.method = method;
    }

    @Override
    protected String collapse() {
        StringJoiner params = new StringJoiner(", ");
        for (ExpressionTree arg : args) params.add(arg.asExpression());
        return method.getFunctionName() + "(" + params + ")";
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
