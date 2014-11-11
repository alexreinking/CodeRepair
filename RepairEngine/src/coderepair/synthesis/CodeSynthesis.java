package coderepair.synthesis;

import coderepair.SynthesisGraph;
import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaGraphNode;
import coderepair.analysis.JavaTypeNode;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.ClosestFirstIterator;

import java.util.*;
import java.util.stream.Collectors;

public class CodeSynthesis {
    private final SynthesisGraph synthesisGraph;
    private final Map<JavaTypeNode, SortedSet<Generator>> synthTable = new HashMap<>();
    private final Map<JavaTypeNode, SortedSet<CodeSnippet>> snippetTable = new HashMap<>();

    public Map<JavaTypeNode, SortedSet<CodeSnippet>> getEnforcedSnippets() {
        return enforcedSnippets;
    }

    private final Map<JavaTypeNode, SortedSet<CodeSnippet>> enforcedSnippets = new HashMap<>();
    private double costLimit = 0.0;

    public CodeSynthesis(SynthesisGraph synthesisGraph) {
        this.synthesisGraph = synthesisGraph;
    }

    public SortedSet<CodeSnippet> synthesize(String qualifiedName, double costLimit, int nRequested) {
        synthTable.clear();
        snippetTable.clear();
        this.costLimit = costLimit;

        JavaGraphNode requestedType = synthesisGraph.getTypeByName(qualifiedName);
        Stack<JavaTypeNode> typesByDistance = new Stack<>();

        ClosestFirstIterator<JavaGraphNode, DefaultWeightedEdge> costLimitBall
                = new ClosestFirstIterator<>(synthesisGraph, requestedType, costLimit);
        while (costLimitBall.hasNext()) {
            JavaGraphNode next = costLimitBall.next();
            if (next instanceof JavaFunctionNode) {
                JavaTypeNode output = ((JavaFunctionNode) next).getOutput();
                synthTable.computeIfAbsent(output, t -> new BoundedSortedSet<>(nRequested))
                        .add(new Generator(next, synthesisGraph.getWeight(output, next)));
            } else typesByDistance.push((JavaTypeNode) next);
        }

        snippetTable.putAll(enforcedSnippets);

        while (!typesByDistance.empty()) {
            JavaTypeNode top = typesByDistance.pop();
            double costDeficit = costLimitBall.getShortestPathLength(top);
            double effectiveCostLimit = costLimit - costDeficit;
            snippetTable.put(top, getExpression(top, effectiveCostLimit, nRequested));
        }

        return snippetTable.getOrDefault(requestedType, Collections.emptySortedSet());
    }

    public void enforce(String type, CodeSnippet snippet) {
        enforcedSnippets.computeIfAbsent(synthesisGraph.getTypeByName(type), v -> new TreeSet<>()).add(snippet);
    }

    public void strongEnforce(String type, CodeSnippet snippet) {
        for (JavaTypeNode assignableType : synthesisGraph.getAssignableTypes(synthesisGraph.getTypeByName(type)))
            enforce(assignableType.getName(), snippet);
    }

    public void relax() {
        enforcedSnippets.clear();
    }

    SortedSet<CodeSnippet> getExpression(JavaTypeNode requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null) return Collections.emptySortedSet();
        if (snippetTable.containsKey(requestedType)) return snippetTable.get(requestedType);

        SortedSet<CodeSnippet> snippets = Collections.synchronizedSortedSet(new BoundedSortedSet<>(nRequested));
        synthTable.get(requestedType)
                .parallelStream()
                .filter(generator -> generator.cost <= remaining)
                .forEach(generator -> {
                    if (generator.type instanceof JavaFunctionNode) {
                        JavaFunctionNode funcGen = (JavaFunctionNode) generator.type;

                        List<SortedSet<CodeSnippet>> choices =
                                funcGen.getSignature()
                                        .stream()
                                        .map(input -> getExpression(input, remaining - generator.cost, nRequested))
                                        .collect(Collectors.toList());

                        addFunctionPossibilities(
                                snippets, funcGen, generator.cost, choices, 0,
                                new CodeSnippet[choices.size()]);
                    }
                });

        return snippets;
    }

    void addSnippet(SortedSet<CodeSnippet> snippets, CodeSnippet poss, int nRequested) {
        if (snippets.size() >= nRequested) {
            CodeSnippet worstInSet = snippets.last();
            if (worstInSet == null) return;
            snippets.remove(worstInSet);
            if (poss.compareTo(worstInSet) < 0) worstInSet = poss;
            snippets.add(worstInSet);
        } else {
            snippets.add(poss);
        }
    }

    void addFunctionPossibilities(
            SortedSet<CodeSnippet> snippets, JavaFunctionNode functionType,
            double currentCost, List<SortedSet<CodeSnippet>> synths,
            int pos, CodeSnippet paramArray[]) {
        if (pos == paramArray.length) {
            snippets.add(new CodeSnippet(functionType.synthesize(paramArray), currentCost));
        } else {
            for (CodeSnippet snip : synths.get(pos)) {
                paramArray[pos] = snip;
                double nextCost = currentCost + snip.cost;
                if (nextCost > costLimit) break; // thanks, sorted order!
                addFunctionPossibilities(snippets, functionType, nextCost,
                        synths, pos + 1, paramArray);
            }
        }
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