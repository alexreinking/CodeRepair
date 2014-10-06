package coderepair;

import coderepair.analysis.*;
import org.jetbrains.annotations.NotNull;
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
import java.util.TreeSet;

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
    private final double costLimit;

    public SynthesisGraph(JavaTypeBuilder nodeManager) {
        this(nodeManager, 20.0);
    }

    public SynthesisGraph(JavaTypeBuilder nodeManager, double costLimit) {
        super(DefaultWeightedEdge.class);
        this.nodeManager = nodeManager;
        this.costLimit = costLimit;
    }

    public JavaTypeBuilder getNodeManager() {
        return nodeManager;
    }

    public void exportToFile(Writer outputStream) {
        new DOTExporter<JavaType, DefaultWeightedEdge>(idProvider, nameProvider, null, colorProvider, null)
                .export(outputStream, this);
    }

    public void synthesizeType(String qualifiedName) {
        HashMap<JavaType, TreeSet<Generator>> synthTable = new HashMap<JavaType, TreeSet<Generator>>();
        JavaType requestedType = nodeManager.getTypeFromName(qualifiedName);
        satisfyType(requestedType, synthTable, 0.0);

        dumpTable(synthTable);
        buildType(requestedType, synthTable, 0);
    }

    private void buildType(JavaType toGen, HashMap<JavaType, TreeSet<Generator>> synthTable, int depth) {

    }

    private void dumpTable(HashMap<JavaType, TreeSet<Generator>> synthTable) {
        for (Map.Entry<JavaType, TreeSet<Generator>> entry : synthTable.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            System.out.println("Generators for " + entry.getKey().getName() + ":");
            for (Generator fragment : entry.getValue()) {

                System.out.println("\t" + fragment.node.getName() + ":" + fragment.cost);
            }
            System.out.println();
        }
    }

    private boolean satisfyType(JavaType startType,
                                HashMap<JavaType, TreeSet<Generator>> synthTable,
                                double cost) {
        if (cost > costLimit) return false;
        if (!synthTable.containsKey(startType)) {
            TreeSet<Generator> fragments = new TreeSet<Generator>();
            synthTable.put(startType, fragments);

            for (JavaType funcType : Graphs.successorListOf(this, startType)) {
                double edgeWeight = getEdgeWeight(getEdge(startType, funcType));
                if (satisfyFunction(funcType, synthTable, cost + edgeWeight))
                    fragments.add(new Generator(funcType, edgeWeight));
            }
        }
        return !synthTable.get(startType).isEmpty();
    }

    private boolean satisfyFunction(JavaType funcType,
                                    HashMap<JavaType, TreeSet<Generator>> synthTable,
                                    double cost) {
        if (cost > costLimit) return false;
        boolean satisfied = true;
        for (JavaType inputType : Graphs.successorListOf(this, funcType))
            satisfied &= satisfyType(inputType, synthTable, cost + getEdgeWeight(getEdge(funcType, inputType)));
        if (funcType instanceof JavaMethodType) {
            JavaType owner = ((JavaMethodType) funcType).getOwner();
            satisfied &= satisfyType(owner, synthTable, cost + getEdgeWeight((getEdge(funcType, owner))));
        }
        return satisfied;
    }

    public void addLocalVariable(JavaValueType javaValueType) {
        addVertex(javaValueType);
        DefaultWeightedEdge newEdge = addEdge(javaValueType.getOutput(), javaValueType);
        setEdgeWeight(newEdge, 0.0);
    }

    private static class Generator implements Comparable<Generator> {
        public JavaType node;
        public double cost;

        private Generator(JavaType node, double cost) {
            this.node = node;
            this.cost = cost;
        }

        @Override public int compareTo(@NotNull Generator o) {
            if (cost != o.cost)
                return Double.compare(cost, o.cost);
            return node.getName().compareTo(o.node.getName());
        }
    }
}
