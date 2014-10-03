package coderepair;

import coderepair.analysis.JavaType;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class SynthesisGraph extends SimpleDirectedWeightedGraph<JavaType, DefaultWeightedEdge> {
    private final JavaTypeBuilder nodeManager;

    public SynthesisGraph(JavaTypeBuilder nodeManager) {
        super(DefaultWeightedEdge.class);
        this.nodeManager = nodeManager;
    }

    public JavaTypeBuilder getNodeManager() {
        return nodeManager;
    }

    void synthesizeType(String qualifiedName) {

    }
}
