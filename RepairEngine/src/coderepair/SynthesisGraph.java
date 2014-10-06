package coderepair;

import coderepair.analysis.JavaClassType;
import coderepair.analysis.JavaPrimitiveType;
import coderepair.analysis.JavaType;
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
        this(nodeManager, 8.0);
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
        HashMap<JavaType, TreeSet<SynthesisFragment>> synthTable = new HashMap<JavaType, TreeSet<SynthesisFragment>>();
        satisfyType(nodeManager.getTypeFromName(qualifiedName), synthTable, 0.0);

        for (Map.Entry<JavaType, TreeSet<SynthesisFragment>> entry : synthTable.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            System.out.println("Generators for " + entry.getKey().getName() + ":");
            for (SynthesisFragment fragment : entry.getValue())
                System.out.println("\t" + fragment.node.getName() + ":" + fragment.cost);
            System.out.println();
        }
    }

    private boolean satisfyType(JavaType startType,
                                HashMap<JavaType, TreeSet<SynthesisFragment>> synthTable,
                                double cost) {
        if (!synthTable.containsKey(startType)) {
            TreeSet<SynthesisFragment> fragments = new TreeSet<SynthesisFragment>();
            synthTable.put(startType, fragments);

            for (JavaType funcType : Graphs.successorListOf(this, startType)) {
                double edgeWeight = getEdgeWeight(getEdge(startType, funcType));
                if (cost + edgeWeight < costLimit)
                    if (satisfyFunction(funcType, synthTable, cost + edgeWeight))
                        fragments.add(new SynthesisFragment(funcType, edgeWeight));
            }
        }
        return !synthTable.get(startType).isEmpty();
    }

    private boolean satisfyFunction(JavaType funcType,
                                    HashMap<JavaType, TreeSet<SynthesisFragment>> synthTable,
                                    double cost) {
        boolean satisfied = true;
        if (Graphs.successorListOf(this, funcType).size() > 0)
            for (JavaType inputType : Graphs.successorListOf(this, funcType))
                satisfied &= satisfyType(inputType, synthTable, cost + getEdgeWeight(getEdge(funcType, inputType)));
        return satisfied;
    }

    private static class SynthesisFragment implements Comparable<SynthesisFragment> {
        public JavaType node;
        public double cost;

        private SynthesisFragment(JavaType node, double cost) {
            this.node = node;
            this.cost = cost;
        }

        @Override public int compareTo(@NotNull SynthesisFragment o) {
            if (cost != o.cost)
                return Double.compare(cost, o.cost);
            return node.getName().compareTo(o.node.getName());
        }
    }
}
