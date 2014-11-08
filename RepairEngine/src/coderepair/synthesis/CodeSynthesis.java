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
    private final Map<JavaGraphNode, SortedSet<Generator>> synthTable = new HashMap<>();
    private final Map<JavaGraphNode, SortedSet<CodeSnippet>> snippetTable = new HashMap<>();
    private double costLimit = 0.0;

    public CodeSynthesis(SynthesisGraph synthesisGraph) {
        this.synthesisGraph = synthesisGraph;
    }

    public SortedSet<CodeSnippet> synthesize(String qualifiedName, double costLimit, int nRequested) {
        synthTable.clear();
        snippetTable.clear();
        this.costLimit = costLimit;

        JavaGraphNode requestedType = synthesisGraph.getVertexByName(qualifiedName);

        ClosestFirstIterator<JavaGraphNode, DefaultWeightedEdge> costLimitBall
                = new ClosestFirstIterator<>(synthesisGraph, requestedType, costLimit);
        while (costLimitBall.hasNext()) {
            JavaGraphNode next = costLimitBall.next();
            if (next instanceof JavaFunctionNode) {
                JavaTypeNode output = ((JavaFunctionNode) next).getOutput();
                synthTable.computeIfAbsent(output, t -> new TreeSet<>())
                        .add(new Generator(next, synthesisGraph.getWeight(output, next)));
            }
        }

        return getExpression(requestedType, costLimit, nRequested);
    }

    SortedSet<CodeSnippet> getExpression(JavaGraphNode requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null)
            return new TreeSet<>();
        if (snippetTable.containsKey(requestedType))
            return snippetTable.get(requestedType);

        SortedSet<CodeSnippet> snippets = new TreeSet<>();
        synthTable.get(requestedType)
                .stream()
                .filter(generator -> generator.cost <= remaining)
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

    void addFunctionPossibilities(
            SortedSet<CodeSnippet> snippets, JavaFunctionNode functionType,
            double currentCost, List<SortedSet<CodeSnippet>> synths,
            int pos, CodeSnippet paramArray[], int nRequested) {
        if (synths.size() == 0) {
            addSnippet(snippets, new CodeSnippet(functionType.synthesize(new CodeSnippet[]{}), currentCost), nRequested);
        } else if (pos == paramArray.length) {
            addSnippet(snippets, new CodeSnippet(functionType.synthesize(paramArray), currentCost), nRequested);
        } else {
            for (CodeSnippet snip : synths.get(pos)) {
                paramArray[pos] = snip;
                double nextCost = currentCost + snip.cost;
                if (nextCost > costLimit) break; // thanks, sorted order!
                addFunctionPossibilities(snippets, functionType, nextCost,
                        synths, pos + 1, paramArray, nRequested);
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