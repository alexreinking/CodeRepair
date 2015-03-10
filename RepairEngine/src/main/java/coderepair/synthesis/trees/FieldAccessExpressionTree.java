package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alexreinking on 3/10/15.
 */
public class FieldAccessExpressionTree extends ExpressionTree {
    private final ExpressionTree instance;
    private final JavaFunctionNode field;

    public FieldAccessExpressionTree(JavaFunctionNode field, ExpressionTree instance) {
        this.field = field;
        this.instance = instance;
    }

    @Override
    protected String collapse() {
        return String.format("(%s).%s", instance.asExpression(), field.getFunctionName());
    }

    @Override
    public List<ExpressionTree> getChildren() {
        return Arrays.asList(instance);
    }

    @Override
    public JavaFunctionNode getLeaf() {
        return field;
    }
}
