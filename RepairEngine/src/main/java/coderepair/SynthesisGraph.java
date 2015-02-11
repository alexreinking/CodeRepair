package coderepair;

import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaGraphNode;
import coderepair.analysis.JavaTypeNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SynthesisGraph extends SimpleDirectedWeightedGraph<JavaGraphNode, DefaultWeightedEdge>
        implements Serializable, DirectedGraph<JavaGraphNode, DefaultWeightedEdge> {
    private static final IntegerNameProvider<JavaGraphNode> idProvider = new IntegerNameProvider<>();
    private static final VertexNameProvider<JavaGraphNode> nameProvider = node -> {
        if (node instanceof JavaFunctionNode)
            return ((JavaFunctionNode) node).getFunctionName();
        return node.getName();
    };
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

    public ArrayList<JavaGraphNode> getCurrentLocals() {
        return currentLocals;
    }

    public void exportToFile(Writer outputStream) {
        exportToFile(outputStream, this);
    }

    public void serialize(OutputStream outputStream) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(this);
        out.close();
    }

    public static void exportToFile(Writer outputStream, Graph<JavaGraphNode, DefaultWeightedEdge> graph) {
        new DOTExporter<JavaGraphNode, DefaultWeightedEdge>(idProvider, nameProvider, null, colorProvider, null)
                .export(outputStream, graph);
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
                .filter(javaGraphNode -> ((JavaFunctionNode )javaGraphNode).getFunctionName().equals("<cast>"))
                .forEach(javaGraphNode -> {
                    JavaFunctionNode functionNode = (JavaFunctionNode) javaGraphNode;
                    JavaTypeNode superType = functionNode.getOutput();
                    superTypes.addAll(getAssignableTypes(superType));
                });
        return superTypes;
    }

    // TODO: make this not suck
    public JavaFunctionNode lookupFunction(String functionName, String outputType, List<String> argumentTypes) {
        if (functionName.isEmpty()) return null;
        String formalName = String.format("%s: (%s) -> %s", functionName, String.join(" x ", argumentTypes), outputType);

        JavaFunctionNode functionNode = null;

        try {
            JavaGraphNode outputVertex = getTypeByName(outputType);

            for (JavaGraphNode generatorNode : Graphs.successorListOf(this, outputVertex))
                if (generatorNode instanceof JavaFunctionNode) {
                    JavaFunctionNode currentCandidate = (JavaFunctionNode) generatorNode;
                    if (functionName.equals(currentCandidate.getFunctionName())) {
                        List<String> actualTypes = currentCandidate.getSignature().stream()
                                .map(JavaTypeNode::getName).collect(Collectors.toList());

                        if (formalName.equals(currentCandidate.getName())) {
                            functionNode = currentCandidate;
                            break;
                        } else if (argumentsMatch(actualTypes, argumentTypes))
                            functionNode = currentCandidate;
                    }
                }

        } catch (IllegalArgumentException e) {
            System.err.printf("No output type %s found in graph.%n", outputType);
        }

        return functionNode;
    }

    // TODO: This is a Seppuku-worthy hack. It should not be.
    private boolean argumentsMatch(List<String> actualTypes, List<String> givenTypes) {
        if (actualTypes.size() != givenTypes.size())
            return false;

        try {
            Iterator<String> actualIt = actualTypes.iterator();
            Iterator<String> givenIt = givenTypes.iterator();
            while (actualIt.hasNext() && givenIt.hasNext())
                // The shame
                if (!Class.forName(actualIt.next()).isAssignableFrom(Class.forName(givenIt.next())))
                    return false;
            return true;
        } catch (Exception e) {
            return false;
        }
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