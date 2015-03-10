package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.Collections;
import java.util.List;

/**
 * Created by alexreinking on 3/10/15.
 */
public class ValueExpressionTree extends ExpressionTree {
    private final JavaFunctionNode value;

    public ValueExpressionTree(JavaFunctionNode value) {
        this.value = value;
    }

    @Override
    protected String collapse() {
        return value.getFunctionName();
    }

    @Override
    public List<ExpressionTree> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public JavaFunctionNode getLeaf() {
        return value;
    }
}
