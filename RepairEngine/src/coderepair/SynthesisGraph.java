package coderepair;

import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaGraphNode;
import coderepair.analysis.JavaTypeNode;
import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.CodeSynthesis;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.Serializable;
import java.io.Writer;
import java.util.*;

public class SynthesisGraph extends SimpleDirectedWeightedGraph<JavaGraphNode, DefaultWeightedEdge>
        implements Serializable {
    private static final IntegerNameProvider<JavaGraphNode> idProvider = new IntegerNameProvider<>();
    private static final VertexNameProvider<JavaGraphNode> nameProvider = JavaGraphNode::getName;
    private static final ComponentAttributeProvider<JavaGraphNode> colorProvider = component -> {
        if (component instanceof JavaTypeNode) {
            HashMap<String, String> attrMap = new HashMap<>();
            attrMap.put("fontcolor", "white");
            attrMap.put("fillcolor", "blue");
            attrMap.put("shape", "box");
            attrMap.put("style", "filled");
            return attrMap;
        }
        return null;
    };

    private final JavaTypeBuilder nodeManager;
    private final ArrayList<JavaGraphNode> currentLocals = new ArrayList<>();

    public SynthesisGraph(JavaTypeBuilder nodeManager) {
        super(DefaultWeightedEdge.class);
        this.nodeManager = nodeManager;
    }

    public void exportToFile(Writer outputStream) {
        new DOTExporter<JavaGraphNode, DefaultWeightedEdge>(idProvider, nameProvider, null, colorProvider, null)
                .export(outputStream, this);
    }

    public JavaGraphNode getVertexByName(String qualifiedName) {
        return nodeManager.getTypeFromName(qualifiedName);
    }

    public double getWeight(JavaGraphNode startType, JavaGraphNode funcType) {
        return getEdgeWeight(getEdge(startType, funcType));
    }

    public void addLocalVariable(String value, String qualifiedType) {
        JavaFunctionNode newLocal = nodeManager.makeValue(value, qualifiedType);
        if (addVertex(newLocal)) {
            currentLocals.add(newLocal);
            setEdgeWeight(addEdge(newLocal.getOutput(), newLocal), -1.0);
        }
    }

    public void resetLocals() {
        currentLocals.forEach(this::removeVertex);
        currentLocals.clear();
    }

}
