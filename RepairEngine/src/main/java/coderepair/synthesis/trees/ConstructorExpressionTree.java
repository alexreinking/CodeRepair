package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        ArrayList<String> formals = new ArrayList<>(args.length);
        for (ExpressionTree arg : args) formals.add(arg.asExpression());
        return String.format("new %s(%s)", ctor.getFunctionName(), String.join(", ", formals));
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
