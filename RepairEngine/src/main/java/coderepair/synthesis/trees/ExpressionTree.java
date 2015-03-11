package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.List;

/**
 * Created by alexreinking on 3/10/15.
 */
public abstract class ExpressionTree {
    private String expr = null;

    public abstract List<ExpressionTree> getChildren();

    @Override
    public final String toString() {
        return asExpression();
    }

    public final String asExpression() {
        if (expr == null)
            expr = collapse();
        return expr;
    }

    protected abstract String collapse();

    public abstract JavaFunctionNode getLeaf();
}
