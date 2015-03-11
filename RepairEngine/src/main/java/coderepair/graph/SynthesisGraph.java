package coderepair.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SynthesisGraph extends SimpleDirectedWeightedGraph<JavaGraphNode, DefaultWeightedEdge>
        implements Serializable, DirectedGraph<JavaGraphNode, DefaultWeightedEdge> {

    private final JavaGraphNodeFactory nodeManager;
    private final ArrayList<JavaGraphNode> currentLocals = new ArrayList<>();

    public SynthesisGraph(JavaGraphNodeFactory nodeManager) {
        super(DefaultWeightedEdge.class);
        this.nodeManager = nodeManager;
    }

    public void serialize(OutputStream outputStream) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(this);
        out.close();
    }

    public boolean hasType(String qualifiedName) {
        return nodeManager.hasType(qualifiedName);
    }

    public JavaTypeNode getTypeByName(String qualifiedName) {
        return nodeManager.getTypeByName(qualifiedName);
    }

    public List<JavaTypeNode> getAssignableTypes(JavaTypeNode thisType) {
        List<JavaTypeNode> superTypes = new LinkedList<>();
        superTypes.add(thisType);
        Graphs.predecessorListOf(this, thisType).stream()
                .filter(javaGraphNode -> javaGraphNode instanceof JavaFunctionNode)
                .filter(javaGraphNode -> ((JavaFunctionNode) javaGraphNode).getFunctionName().equals("<cast>"))
                .forEach(javaGraphNode -> {
                    JavaFunctionNode functionNode = (JavaFunctionNode) javaGraphNode;
                    JavaTypeNode superType = functionNode.getOutput();
                    superTypes.addAll(getAssignableTypes(superType));
                });
        return superTypes;
    }

    public double getWeight(JavaGraphNode startType, JavaGraphNode funcType) {
        return getEdgeWeight(getEdge(startType, funcType));
    }

    public void addLocalVariable(String value, String qualifiedType) {
        addLocalVariable(value, qualifiedType, 0.001);
    }

    public void addLocalVariable(String value, String qualifiedType, double desiredCost) {
        JavaFunctionNode newLocal = nodeManager.makeValue(value, qualifiedType);
        if (addVertex(newLocal)) {
            currentLocals.add(newLocal);
            setEdgeWeight(addEdge(newLocal.getOutput(), newLocal), desiredCost);
        }
    }

    public void resetLocals() {
        currentLocals.forEach(this::removeVertex);
        currentLocals.clear();
    }

}
