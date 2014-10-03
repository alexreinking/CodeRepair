package coderepair;

import coderepair.analysis.JavaClassType;
import coderepair.analysis.JavaPrimitiveType;
import coderepair.analysis.JavaType;
import org.jgrapht.Graphs;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class SynthesisGraph extends SimpleDirectedWeightedGraph<JavaType, DefaultWeightedEdge> {
    private static IntegerNameProvider<JavaType> idProvider = new IntegerNameProvider<JavaType>();

    private static VertexNameProvider<JavaType> nameProvider = new VertexNameProvider<JavaType>() {
        @Override public String getVertexName(JavaType type) {
            return type.getName();
        }
    };

    private static ComponentAttributeProvider<JavaType> colorProvider = new ComponentAttributeProvider<JavaType>() {
        @Override public Map<String, String> getComponentAttributes(JavaType component) {
            if (component instanceof JavaClassType || component instanceof JavaPrimitiveType) {
                HashMap<String, String> attrMap = new HashMap<String, String>();
                attrMap.put("fontcolor", "white");
                attrMap.put("fillcolor", "blue");
                attrMap.put("shape", "box");
                attrMap.put("style", "filled");
                return attrMap;
            }
            return null;
        }
    };

    private final JavaTypeBuilder nodeManager;

    public SynthesisGraph(JavaTypeBuilder nodeManager) {
        super(DefaultWeightedEdge.class);
        this.nodeManager = nodeManager;
    }

    public JavaTypeBuilder getNodeManager() {
        return nodeManager;
    }

    public void exportToFile(Writer outputStream) {
        new DOTExporter<JavaType, DefaultWeightedEdge>(idProvider, nameProvider, null, colorProvider, null)
                .export(outputStream, this);
    }

    void synthesizeType(String qualifiedName) {
        JavaType startType = nodeManager.getTypeFromName(qualifiedName);

        for (JavaType succType : Graphs.successorListOf(this, startType)) {
//            System.out.printf("%s <=> %f\n", succType.getName(), getEdgeWeight(getEdge(startType, succType)));
        }
    }
}
