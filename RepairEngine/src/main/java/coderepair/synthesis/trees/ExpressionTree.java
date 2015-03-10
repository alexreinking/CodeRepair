package coderepair.synthesis.trees;

import coderepair.graph.JavaFunctionNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Created by alexreinking on 3/10/15.
 */
public abstract class ExpressionTree implements Comparable<ExpressionTree> {
    private String expr = null;
    private Double cost = null;

    public final Double getCost() {
        return cost;
    }

    public final void setCost(double cost) {
        assert this.cost == null;
        this.cost = cost;
    }

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

    @Override
    public int compareTo(@NotNull ExpressionTree rhs) {
        if (!Objects.equals(this.cost, rhs.cost))
            return Double.compare(this.cost, rhs.cost);
        return asExpression().compareTo(rhs.asExpression());
    }
}
