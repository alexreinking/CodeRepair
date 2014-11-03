package coderepair;

import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaGraphNode;
import coderepair.analysis.JavaTypeNode;
import coderepair.synthesis.CodeSnippet;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graphs;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.Serializable;
import java.io.Writer;
import java.util.*;

public class SynthesisGraph extends SimpleDirectedWeightedGraph<JavaGraphNode, DefaultWeightedEdge> implements Serializable {
    private static final IntegerNameProvider<JavaGraphNode> idProvider = new IntegerNameProvider<JavaGraphNode>();

    private static final VertexNameProvider<JavaGraphNode> nameProvider = new VertexNameProvider<JavaGraphNode>() {
        @Override
        public String getVertexName(JavaGraphNode type) {
            return type.getName();
        }
    };

    private static final ComponentAttributeProvider<JavaGraphNode> colorProvider = new ComponentAttributeProvider<JavaGraphNode>() {
        @Override
        public Map<String, String> getComponentAttributes(JavaGraphNode component) {
            if (component instanceof JavaTypeNode) {
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
    private final ArrayList<JavaGraphNode> currentLocals = new ArrayList<JavaGraphNode>();

    private transient HashMap<JavaGraphNode, TreeSet<Generator>> synthTable;
    private transient HashMap<JavaGraphNode, TreeSet<CodeSnippet>> snippetTable;

    private transient HashMap<JavaGraphNode, Double> synthCost;

    public SynthesisGraph(JavaTypeBuilder nodeManager) {
        this(nodeManager, 10.0);
    }

    public SynthesisGraph(JavaTypeBuilder nodeManager, double costLimit) {
        super(DefaultWeightedEdge.class);
        this.nodeManager = nodeManager;
        this.costLimit = costLimit;
    }

    public void exportToFile(Writer outputStream) {
        new DOTExporter<JavaGraphNode, DefaultWeightedEdge>(idProvider, nameProvider, null, colorProvider, null)
                .export(outputStream, this);
    }

    public JavaGraphNode getVertexByName(String qualifiedName) {
        return nodeManager.getTypeFromName(qualifiedName);
    }

    public TreeSet<CodeSnippet> synthesize(String qualifiedName, int nRequested) {
        synthTable = new HashMap<JavaGraphNode, TreeSet<Generator>>();
        snippetTable = new HashMap<JavaGraphNode, TreeSet<CodeSnippet>>();
        synthCost = new HashMap<JavaGraphNode, Double>();

        JavaGraphNode requestedType = nodeManager.getTypeFromName(qualifiedName);
        depth = 0;
        satisfyType(requestedType, 0.0);

        Iterator<Map.Entry<JavaGraphNode, TreeSet<Generator>>> iterator = synthTable.entrySet().iterator();
        while (iterator.hasNext())
            if (iterator.next().getValue().isEmpty())
                iterator.remove();

//        dumpTable();
        return getExpression(requestedType, costLimit, nRequested);
    }

    private TreeSet<CodeSnippet> getExpression(JavaGraphNode requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null)
            return new TreeSet<CodeSnippet>();
        if (snippetTable.containsKey(requestedType))
            return snippetTable.get(requestedType);

        TreeSet<CodeSnippet> snippets = new TreeSet<CodeSnippet>();
        for (Generator generator : synthTable.get(requestedType))
            if (generator.cost < remaining) {
                double nextCost = remaining - generator.cost;
                if (generator.type instanceof JavaFunctionNode) {
                    JavaFunctionNode fGen = (JavaFunctionNode) generator.type;
                    List<TreeSet<CodeSnippet>> choices = new ArrayList<TreeSet<CodeSnippet>>();
                    for (JavaGraphNode input : fGen.getSignature())
                        choices.add(getExpression(input, nextCost, nRequested));
                    addFunctionPossibilities(
                            snippets, fGen, generator.cost, choices, 0,
                            new CodeSnippet[choices.size()], nRequested);
                }
            }

        if (!snippets.isEmpty())
            snippetTable.put(requestedType, snippets);
        return snippets;
    }

    private void addSnippet(TreeSet<CodeSnippet> snippets, CodeSnippet poss, int nRequested) {
        if (poss.cost <= costLimit)
            if (snippets.size() >= nRequested) {
                CodeSnippet worstInSet = snippets.pollLast();
                if (worstInSet == null) return;
                if (poss.compareTo(worstInSet) < 0) worstInSet = poss;
                snippets.add(worstInSet);
            } else {
                snippets.add(poss);
            }
    }

    private double sumSnips(CodeSnippet snips[]) {
        double tot = 0.0;
        for (CodeSnippet snip : snips) tot += snip.cost;
        return tot;
    }

    private void addFunctionPossibilities(
            TreeSet<CodeSnippet> snippets, JavaFunctionNode functionType,
            double baseCost, List<TreeSet<CodeSnippet>> synths,
            int pos, CodeSnippet paramArray[], int nRequested) {
        if (synths.size() == 0) {
            String code = functionType.synthesize(new CodeSnippet[]{});
            addSnippet(snippets, new CodeSnippet(code, baseCost), nRequested);
        } else {
            TreeSet<CodeSnippet> snips = synths.get(pos);
            if (pos + 1 == paramArray.length) {
                for (CodeSnippet snip : snips) {
                    paramArray[pos] = snip;
                    String code = functionType.synthesize(paramArray);
                    double cost = sumSnips(paramArray);
                    addSnippet(snippets, new CodeSnippet(code, baseCost + cost), nRequested);
                }
            } else {
                for (CodeSnippet snip : snips) {
                    paramArray[pos] = snip;
                    addFunctionPossibilities(snippets, functionType, baseCost, synths, pos + 1, paramArray, nRequested);
                }
            }
        }
    }

    private void dumpTable() {
        for (Map.Entry<JavaGraphNode, TreeSet<Generator>> entry : synthTable.entrySet()) {
            System.out.println("Generators for " + entry.getKey().getName() + ":");
            for (Generator fragment : entry.getValue())
                System.out.printf("\t%6f  %s%n", fragment.cost, fragment.type.getName());
            System.out.println();
        }
    }

    private String tabbing(int depth) {
        return new String(new char [depth]).replace("\0", "    ");
    }

    private transient int depth;
    private boolean satisfyType(JavaGraphNode startType, double cost) {
        TreeSet<Generator> fragments = synthTable.computeIfAbsent(startType, n -> new TreeSet<>());
        System.out.println(tabbing(depth) + startType.getName());
        depth++;
        if (costLimit > cost && cost < synthCost.getOrDefault(startType, Double.MAX_VALUE)) {
            synthCost.put(startType, cost);

            try {
                for (JavaGraphNode funcType : Graphs.successorListOf(this, startType)) {
                    double edgeWeight = getEdgeWeight(getEdge(startType, funcType));
                    if (satisfyFunction(funcType, cost + edgeWeight))
                        fragments.add(new Generator(funcType, edgeWeight));
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to satisfy " + startType.getName());
                throw e;
            }
        }
        depth--;
        return !synthTable.get(startType).isEmpty();
    }

    private boolean satisfyFunction(JavaGraphNode funcType, double cost) {
        if (cost > costLimit) return false;
        boolean satisfied = true;
        for (JavaGraphNode inputType : Graphs.successorListOf(this, funcType))
            satisfied &= satisfyType(inputType, cost + getEdgeWeight(getEdge(funcType, inputType)));
        return satisfied;
    }

    public void addLocalVariable(String value, String qualifiedType) {
        JavaFunctionNode newLocal = nodeManager.makeValue(value, qualifiedType);
        if (addVertex(newLocal)) {
            currentLocals.add(newLocal);
            setEdgeWeight(addEdge(newLocal.getOutput(), newLocal), -1.0);
        }
    }

    public void resetLocals() {
        for (JavaGraphNode currentLocal : currentLocals) removeVertex(currentLocal);
        currentLocals.clear();
    }

    private static class Generator implements Comparable<Generator> {
        public final JavaGraphNode type;
        public final double cost;

        private Generator(JavaGraphNode type, double cost) {
            this.type = type;
            this.cost = cost;
        }

        @Override
        public int compareTo(@NotNull Generator o) {
            if (cost != o.cost)
                return Double.compare(cost, o.cost);
            return type.getName().compareTo(o.type.getName());
        }
    }
}
