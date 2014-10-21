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
    private static final IntegerNameProvider<JavaType> idProvider = new IntegerNameProvider<JavaType>();

    private static final VertexNameProvider<JavaType> nameProvider = new VertexNameProvider<JavaType>() {
        @Override
        public String getVertexName(JavaType type) {
            return type.getName();
        }
    };

    private static final ComponentAttributeProvider<JavaType> colorProvider = new ComponentAttributeProvider<JavaType>() {
        @Override
        public Map<String, String> getComponentAttributes(JavaType component) {
            if (component instanceof JavaClassType) {
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
    private HashMap<JavaType, TreeSet<Snippet>> snippetTable;
    private ArrayList<JavaValueType> currentLocals = new ArrayList<JavaValueType>();

    public SynthesisGraph(JavaTypeBuilder nodeManager) {
        this(nodeManager, 10.0);
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

    public TreeSet<Snippet> synthesize(String qualifiedName, int nRequested) {
        synthTable = new HashMap<JavaType, TreeSet<Generator>>();
        snippetTable = new HashMap<JavaType, TreeSet<Snippet>>();
        JavaType requestedType = nodeManager.getTypeFromName(qualifiedName);
        satisfyType(requestedType, 0.0);

        Iterator<Map.Entry<JavaType, TreeSet<Generator>>> iterator = synthTable.entrySet().iterator();
        while (iterator.hasNext())
            if (iterator.next().getValue().isEmpty())
                iterator.remove();

//        dumpTable();
        return getExpression(requestedType, costLimit, nRequested);
    }

    private TreeSet<Snippet> getExpression(JavaType requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null)
            return new TreeSet<Snippet>();
        if (snippetTable.containsKey(requestedType))
            return snippetTable.get(requestedType);

        TreeSet<Snippet> snippets = new TreeSet<Snippet>();
        for (Generator generator : synthTable.get(requestedType))
            if (generator.cost < remaining) {
                double nextCost = remaining - generator.cost;
                if (generator.type instanceof JavaValueType)
                    snippets.add(new Snippet(generator.type.getName(), generator.cost));
                else if (generator.type instanceof JavaFunctionType) {
                    ArrayList<Snippet> owners = new ArrayList<Snippet>();
                    if (generator.type instanceof JavaMethodType) {
                        TreeSet<Snippet> owner = getExpression(((JavaMethodType) generator.type).getOwner(), nextCost, nRequested);
                        if (owner.size() == 0) continue;
                        owners.addAll(owner);
                    } else owners.add(new Snippet("", 0.0));
                    JavaFunctionType fGen = (JavaFunctionType) generator.type;
                    List<TreeSet<Snippet>> choices = new ArrayList<TreeSet<Snippet>>();
                    for (JavaType input : fGen.getSignature()) choices.add(getExpression(input, nextCost, nRequested));
                    for (Snippet prefix : owners)
                        addFunctionPossibilities(
                                prefix.code, snippets, fGen, generator.cost + prefix.cost, choices, 0,
                                new Snippet[choices.size()], nRequested);
                }
            }

        if (!snippets.isEmpty())
            snippetTable.put(requestedType, snippets);
        return snippets;
    }

    private void addSnippet(TreeSet<Snippet> snippets, Snippet poss, int nRequested) {
        if (poss.cost <= costLimit)
            if (snippets.size() >= nRequested) {
                Snippet better = snippets.pollLast();
                if (better == null) return;
                if (poss.cost < better.cost) better = poss;
                snippets.add(better);
            } else {
                snippets.add(poss);
            }
    }

    private String joinSnips(Snippet snips[]) {
        StringJoiner sj = new StringJoiner(", ");
        for (Snippet snip : snips) sj.add(snip.code);
        return sj.toString();
    }

    private double sumSnips(Snippet snips[]) {
        double tot = 0.0;
        for (Snippet snip : snips) tot += snip.cost;
        return tot;
    }

    private void addFunctionPossibilities(
            String prefix, TreeSet<Snippet> snippets, JavaFunctionType functionType,
            double baseCost, List<TreeSet<Snippet>> synths,
            int pos, Snippet paramArray[], int nRequested) {
        if (synths.size() == 0) {
            String code = functionType.getFunctionName() + "()";
            if (!prefix.isEmpty()) code = String.format("(%s).%s", prefix, code);
            addSnippet(snippets, new Snippet(code, baseCost), nRequested);
        } else {
            TreeSet<Snippet> snips = synths.get(pos);
            if (pos + 1 == paramArray.length) {
                for (Snippet snip : snips) {
                    paramArray[pos] = snip;
                    String params = joinSnips(paramArray);
                    String code;
                    if (functionType instanceof JavaCastType) code = params;
                    else code = functionType.getFunctionName() + "(" + params + ")";
                    if (!prefix.isEmpty()) code = String.format("(%s).%s", prefix, code);
                    double cost = sumSnips(paramArray);
                    addSnippet(snippets, new Snippet(code, baseCost + cost), nRequested);
                }
            } else {
                for (Snippet snip : snips) {
                    paramArray[pos] = snip;
                    addFunctionPossibilities(prefix, snippets, functionType, baseCost, synths, pos + 1, paramArray, nRequested);
                }
            }
        }
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

            try {
                for (JavaType funcType : Graphs.successorListOf(this, startType)) {
                    double edgeWeight = getEdgeWeight(getEdge(startType, funcType));
                    if (satisfyFunction(funcType, cost + edgeWeight))
                        fragments.add(new Generator(funcType, edgeWeight));
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to satisfy " + startType.getName());
                throw e;
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

    public void addLocalVariable(String value, String qualifiedType) {
        JavaValueType newLocal = nodeManager.makeValue(value, qualifiedType);
        if (addVertex(newLocal)) {
            currentLocals.add(newLocal);
            setEdgeWeight(addEdge(newLocal.getOutput(), newLocal), -1.0);
        }
    }

    public void resetLocals() {
        for (JavaValueType currentLocal : currentLocals) removeVertex(currentLocal);
        currentLocals.clear();
    }

    public static class Snippet implements Comparable<Snippet> {
        public final String code;
        public final double cost;

        private Snippet(String code, double cost) {
            this.code = code;
            this.cost = cost;
        }

        @Override
        public int compareTo(@NotNull Snippet o) {
            if (cost != o.cost)
                return Double.compare(cost, o.cost);
            if (code.length() != o.code.length())
                return Integer.compare(code.length(), o.code.length());
            return code.compareTo(o.code);
        }
    }

    private static class Generator implements Comparable<Generator> {
        public final JavaType type;
        public final double cost;

        private Generator(JavaType type, double cost) {
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
