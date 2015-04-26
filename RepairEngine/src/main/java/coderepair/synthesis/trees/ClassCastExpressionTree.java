package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.Collections;
import java.util.List;

/**
 * Created by alexreinking on 3/10/15.
 */
public class ClassCastExpressionTree extends ExpressionTree {
    private final JavaFunctionNode cast;
    private final ExpressionTree argument;

    public ClassCastExpressionTree(JavaFunctionNode cast, ExpressionTree argument) {
        this.cast = cast;
        this.argument = argument;
    }

    @Override
    protected String collapse() {
        return argument.asExpression();
    }

    @Override
    public List<ExpressionTree> getChildren() {
        return Collections.singletonList(argument);
    }

    @Override
    public JavaFunctionNode getLeaf() {
        return cast;
    }
}
