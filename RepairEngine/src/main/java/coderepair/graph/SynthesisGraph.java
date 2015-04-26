package coderepair.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.Serializable;
import java.util.ArrayList;

public class SynthesisGraph extends SimpleDirectedWeightedGraph<JavaGraphNode, DefaultWeightedEdge>
        implements Serializable, DirectedGraph<JavaGraphNode, DefaultWeightedEdge> {

    private final JavaGraphNodeFactory nodeManager;
    private final ArrayList<JavaFunctionNode> currentLocals = new ArrayList<>();

    public SynthesisGraph(JavaGraphNodeFactory nodeManager) {
        super(DefaultWeightedEdge.class);
        this.nodeManager = nodeManager;
    }

    public JavaGraphNodeFactory getNodeManager() {
        return nodeManager;
    }

    public boolean hasType(String qualifiedName) {
        return nodeManager.hasType(qualifiedName);
    }

    public JavaTypeNode getTypeByName(String qualifiedName) {
        return nodeManager.getTypeByName(qualifiedName);
    }

    /*
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
    */

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
