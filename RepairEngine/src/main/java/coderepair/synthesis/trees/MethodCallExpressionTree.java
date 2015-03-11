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
        StringJoiner params = new StringJoiner(", ");
        for (ExpressionTree arg : args) params.add(arg.asExpression());
        return "(" + instance.asExpression() + ")." + method.getFunctionName() + "(" + params + ")";
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
