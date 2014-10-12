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
import java.util.*;

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
    private HashMap<JavaType, TreeSet<Generator>> synthTable;

    public SynthesisGraph(JavaTypeBuilder nodeManager) {
        this(nodeManager, 7.0);
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

    public void synthesize(String qualifiedName) {
        synthTable = new HashMap<JavaType, TreeSet<Generator>>();
        JavaType requestedType = nodeManager.getTypeFromName(qualifiedName);
        satisfyType(requestedType, 0.0);

        Iterator<Map.Entry<JavaType, TreeSet<Generator>>> iterator = synthTable.entrySet().iterator();
        while (iterator.hasNext())
            if (iterator.next().getValue().isEmpty())
                iterator.remove();

        dumpTable();
        for (String expression : getExpression(requestedType)) System.out.println(expression);
    }

    private List<String> getExpression(JavaType requestedType) {
        return new ArrayList<String>();
    }

    private void dumpTable() {
        for (Map.Entry<JavaType, TreeSet<Generator>> entry : synthTable.entrySet()) {
            System.out.println("Generators for " + entry.getKey().getName() + ":");
            for (Generator fragment : entry.getValue()) {
                String member = "";
                if (fragment.type instanceof JavaMethodType)
                    member = " member of " + ((JavaMethodType) fragment.type).getOwner().getName();
                System.out.println("\t" + fragment.type.getName() + ":" + fragment.cost + member);
            }
            System.out.println();
        }
    }

    private boolean satisfyType(JavaType startType, double cost) {
        if (cost > costLimit) return false;
        if (!synthTable.containsKey(startType)) {
            TreeSet<Generator> fragments = new TreeSet<Generator>();
            synthTable.put(startType, fragments);

            for (JavaType funcType : Graphs.successorListOf(this, startType)) {
                double edgeWeight = getEdgeWeight(getEdge(startType, funcType));
                if (satisfyFunction(funcType, cost + edgeWeight))
                    fragments.add(new Generator(funcType, edgeWeight));
            }
        }
        return !synthTable.get(startType).isEmpty();
    }

    private boolean satisfyFunction(JavaType funcType, double cost) {
        if (cost > costLimit) return false;
        boolean satisfied = true;
        for (JavaType inputType : Graphs.successorListOf(this, funcType))
            satisfied &= satisfyType(inputType, cost + getEdgeWeight(getEdge(funcType, inputType)));
        return satisfied;
    }

    public void addLocalVariable(JavaValueType javaValueType) {
        if (addVertex(javaValueType))
            setEdgeWeight(addEdge(javaValueType.getOutput(), javaValueType), 0.0);
    }

    private static class Snippet implements Comparable<Snippet> {
        List<Generator> generators;

        public List<Generator> getGenerators() {
            return generators;
        }

        public void setGenerators(List<Generator> generators) {
            this.generators = generators;
        }

        @Override public int compareTo(@NotNull Snippet o) {
            return Double.compare(totalCost(generators), totalCost(o.generators));
        }

        private double totalCost(List<Generator> generators) {
            double tot = 0.0;
            for (Generator generator : generators) tot += generator.cost;
            return tot;
        }
    }

    private static class Generator implements Comparable<Generator> {
        public JavaType type;
        public double cost;

        private Generator(JavaType type, double cost) {
            this.type = type;
            this.cost = cost;
        }

        @Override public int compareTo(@NotNull Generator o) {
            if (cost != o.cost)
                return Double.compare(cost, o.cost);
            return type.getName().compareTo(o.type.getName());
        }
    }
}
