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
import java.util.stream.Collectors;

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
    private final double costLimit;
    private final ArrayList<JavaGraphNode> currentLocals = new ArrayList<>();

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
        synthTable = new HashMap<>();
        snippetTable = new HashMap<>();
        synthCost = new HashMap<>();

        JavaGraphNode requestedType = nodeManager.getTypeFromName(qualifiedName);
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
            return new TreeSet<>();
        if (snippetTable.containsKey(requestedType))
            return snippetTable.get(requestedType);

        TreeSet<CodeSnippet> snippets = new TreeSet<>();
        synthTable.get(requestedType).stream().filter(generator -> generator.cost < remaining).forEach(generator -> {
            double nextCost = remaining - generator.cost;
            if (generator.type instanceof JavaFunctionNode) {
                JavaFunctionNode fGen = (JavaFunctionNode) generator.type;

                List<TreeSet<CodeSnippet>> choices =
                        fGen.getSignature().stream().map(input -> getExpression(input, nextCost, nRequested))
                                .collect(Collectors.toList());

                addFunctionPossibilities(
                        snippets, fGen, generator.cost, choices, 0,
                        new CodeSnippet[choices.size()], nRequested);
            }
        });

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


    private boolean satisfyType(JavaGraphNode startType, double cost) {
        if (cost > costLimit) return false;

        TreeSet<Generator> fragments = synthTable.computeIfAbsent(startType, t -> new TreeSet<>());
        if (cost < synthCost.getOrDefault(startType, Double.MAX_VALUE)) {
            Graphs.successorListOf(this, startType).stream().filter(funcType -> {
                double functionCost = cost + getWeight(startType, funcType);
                return functionCost <= costLimit
                        && Graphs.successorListOf(this, funcType).stream()
                        .allMatch(inputType -> satisfyType(inputType,
                                functionCost + getWeight(funcType, inputType)));
            }).forEach(funcType -> {
                fragments.add(new Generator(funcType, getWeight(startType, funcType)));
                synthCost.put(startType, cost);
            });
        }

        return !synthTable.get(startType).isEmpty();
    }

    private double getWeight(JavaGraphNode startType, JavaGraphNode funcType) {
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
