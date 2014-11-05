package coderepair.synthesis;

import coderepair.SynthesisGraph;
import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaGraphNode;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graphs;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CodeSynthesis {
    private final SynthesisGraph synthesisGraph;
    private AbstractMap<JavaGraphNode, SortedSet<Generator>> synthTable;
    private AbstractMap<JavaGraphNode, SortedSet<CodeSnippet>> snippetTable;
    private AbstractMap<JavaGraphNode, Double> synthCost;
    private double costLimit = 0.0;

    public CodeSynthesis(SynthesisGraph synthesisGraph) {
        this.synthesisGraph = synthesisGraph;
    }

    public SortedSet<CodeSnippet> synthesize(String qualifiedName, double costLimit, int nRequested) {
        synthTable = new ConcurrentHashMap<>();
        snippetTable = new ConcurrentHashMap<>();
        synthCost = new ConcurrentHashMap<>();
        this.costLimit = costLimit;

        JavaGraphNode requestedType = synthesisGraph.getVertexByName(qualifiedName);
        satisfyType(requestedType, costLimit);

        Iterator<Map.Entry<JavaGraphNode, SortedSet<Generator>>> iterator = synthTable.entrySet().iterator();
        while (iterator.hasNext())
            if (iterator.next().getValue().isEmpty())
                iterator.remove();

//        dumpTable();
        System.out.println("Gathered possibilities...");
        return getExpression(requestedType, costLimit, nRequested);
    }

    SortedSet<CodeSnippet> getExpression(JavaGraphNode requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null)
            return new TreeSet<>();
        if (snippetTable.containsKey(requestedType))
            return snippetTable.get(requestedType);

        TreeSet<CodeSnippet> snippets = new TreeSet<>();
        synthTable.get(requestedType)
                .parallelStream()
                .filter(generator -> generator.cost < remaining)
                .forEach(generator -> {
            double nextCost = remaining - generator.cost;
            if (generator.type instanceof JavaFunctionNode) {
                JavaFunctionNode fGen = (JavaFunctionNode) generator.type;

                List<SortedSet<CodeSnippet>> choices =
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

    void addSnippet(SortedSet<CodeSnippet> snippets, CodeSnippet poss, int nRequested) {
        if (poss.cost < costLimit)
            if (snippets.size() >= nRequested) {
                CodeSnippet worstInSet = snippets.last();
                snippets.remove(worstInSet);
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

    void addFunctionPossibilities(
            SortedSet<CodeSnippet> snippets, JavaFunctionNode functionType,
            double baseCost, List<SortedSet<CodeSnippet>> synths,
            int pos, CodeSnippet paramArray[], int nRequested) {
        if (synths.size() == 0) {
            String code = functionType.synthesize(new CodeSnippet[]{});
            addSnippet(snippets, new CodeSnippet(code, baseCost), nRequested);
        } else {
            SortedSet<CodeSnippet> snips = synths.get(pos);
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

    public void dumpTable() {
        for (Map.Entry<JavaGraphNode, SortedSet<Generator>> entry : synthTable.entrySet()) {
            System.out.println("Generators for " + entry.getKey().getName() + ":");
            for (Generator fragment : entry.getValue())
                System.out.printf("\t%6f  %s%n", fragment.cost, fragment.type.getName());
            System.out.println();
        }
    }

    boolean satisfyType(JavaGraphNode startType, double remaining) {
        if (remaining < 0.0) return false;

        SortedSet<Generator> fragments = synthTable.computeIfAbsent(startType, t -> Collections.synchronizedSortedSet(new TreeSet<>()));
        if (remaining >= synthCost.getOrDefault(startType, 0.0)) {
            Graphs.successorListOf(synthesisGraph, startType).parallelStream().filter(funcType -> {
                double afterFunction = remaining - synthesisGraph.getWeight(startType, funcType);
                return afterFunction >= 0.0
                        && Graphs.successorListOf(synthesisGraph, funcType).parallelStream()
                        .allMatch(inputType -> satisfyType(inputType,
                                afterFunction + synthesisGraph.getWeight(funcType, inputType)));
            }).forEach(funcType -> {
                fragments.add(new Generator(funcType, synthesisGraph.getWeight(startType, funcType)));
                synthCost.put(startType, remaining);
            });
        }

        return !synthTable.get(startType).isEmpty();
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
            if (type.getName().length() != o.type.getName().length())
                return Integer.compare(type.getName().length(), o.type.getName().length());
            return type.getName().compareTo(o.type.getName());
        }
    }
}