package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by alexreinking on 3/10/15.
 */
public class ConstructorExpressionTree extends ExpressionTree {
    private final JavaFunctionNode ctor;
    private final ExpressionTree[] args;

    public ConstructorExpressionTree(JavaFunctionNode ctor, ExpressionTree[] args) {
        this.ctor = ctor;
        this.args = args;
    }

    @Override
    protected String collapse() {
        StringJoiner params = new StringJoiner(", ");
        for (ExpressionTree arg : args) params.add(arg.asExpression());
        return "new " + ctor.getFunctionName() + "(" + params + ")";
    }

    @Override
    public List<ExpressionTree> getChildren() {
        return Arrays.asList(args);
    }

    @Override
    public JavaFunctionNode getLeaf() {
        return ctor;
    }
}
