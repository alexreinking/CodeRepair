package coderepair.weighting;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaGraphNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by alexreinking on 3/13/15.
 */
public class StochasticWeighting implements GraphWeighter {
    @Override
    public void applyWeight(SynthesisGraph graph) {
        graph.vertexSet().forEach(type -> {
            if (type instanceof JavaTypeNode) {
                for (JavaGraphNode nbr : Graphs.successorListOf(graph, type)) {
                    JavaFunctionNode fn = (JavaFunctionNode) nbr;
                    DefaultWeightedEdge edge = graph.getEdge(type, fn);
                    double weight = Math.min(1.0 / Math.log1p(graph.getEdgeWeight(edge)), 10.0);
                    graph.setEdgeWeight(edge, weight);
                    if (fn.getKind().equals(JavaGraphNode.Kind.ClassCast))
                        graph.setEdgeWeight(edge, 0);
                }
            }
        });
    }
}
