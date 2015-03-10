package coderepair.weighting;

import coderepair.SynthesisGraph;
import coderepair.analysis.JavaFunctionNode;

/**
 * Created by alexreinking on 3/9/15.
 */
public class NaiveWeighting implements GraphWeighter {
    @Override
    public void applyWeight(SynthesisGraph graph) {
        graph.edgeSet().forEach(edge -> graph.setEdgeWeight(edge, 0.0));

        graph.vertexSet().stream().filter(node -> node instanceof JavaFunctionNode).forEach(node -> {
            JavaFunctionNode method = (JavaFunctionNode) node;
            graph.setEdgeWeight(graph.getEdge(method.getOutput(), method), costForFunction(method));
        });
    }

    private double costForFunction(JavaFunctionNode method) {
        if (method.getFunctionName().startsWith("new"))
            return 1 + method.getTotalFormals();
        else if (method.getFunctionName().equals("<cast>"))
            return 0.01;
        return 1 + (double) method.getTotalFormals() / 2.0;
    }
}
