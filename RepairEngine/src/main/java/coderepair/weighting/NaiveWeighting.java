package coderepair.weighting;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaGraphNode;
import coderepair.graph.SynthesisGraph;

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
        if (method.getKind() == JavaGraphNode.Kind.Constructor)
            return 1 + method.getTotalFormals();
        else if (method.getKind() == JavaGraphNode.Kind.ClassCast)
            return 0.01;
        return 1 + (double) method.getTotalFormals() / 2.0;
    }
}
