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
    private final Map<JavaFunctionNode, Double> unbiasedWeights = new HashMap<>();
    private final HashMap<JavaGraphNode, Double> costLevel = new HashMap<>();
    private double costLimit = 0.0;

    public CodeSynthesis(SynthesisGraph synthesisGraph) {
        this.synthesisGraph = synthesisGraph;
    }

    public SortedSet<CodeSnippet> synthesize(String qualifiedName, double costLimit, int nRequested) {
        synthTable.clear();
        snippetTable.clear();
        costLevel.clear();
        this.costLimit = costLimit;

        JavaGraphNode requestedType = synthesisGraph.getTypeByName(qualifiedName);
        Stack<JavaTypeNode> typesByDistance = new Stack<>();

        ClosestFirstIterator<JavaGraphNode, DefaultWeightedEdge> costLimitBall
                = new ClosestFirstIterator<>(synthesisGraph, requestedType, costLimit);
        while (costLimitBall.hasNext()) {
            JavaGraphNode next = costLimitBall.next();
            if (next instanceof JavaFunctionNode) {
                JavaTypeNode output = ((JavaFunctionNode) next).getOutput();
                synthTable.computeIfAbsent(output, t -> new TreeSet<>())
                        .add(new Generator(next, synthesisGraph.getWeight(output, next)));
            } else typesByDistance.push((JavaTypeNode) next);
        }

        while (!typesByDistance.empty()) {
            JavaTypeNode top = typesByDistance.pop();
            double costDeficit = costLimitBall.getShortestPathLength(top);
            double effectiveCostLimit = costLimit - costDeficit;
            snippetTable.put(top, getExpression(top, effectiveCostLimit, nRequested));
        }

        return snippetTable.getOrDefault(requestedType, Collections.emptySortedSet());
    }

    public CodeSynthesis biasTowards(Collection<FunctionSignature> signatures) {
        for (FunctionSignature signature : signatures) {
            JavaFunctionNode function = synthesisGraph.lookupFunction(signature.functionName, signature.outputType, signature.argumentTypes);
            if (function != null) {
                System.out.println("Favoring " + function.getName());

                JavaTypeNode output = function.getOutput();
                double oldWeight = synthesisGraph.getWeight(output, function);
                unbiasedWeights.putIfAbsent(function, oldWeight);
                synthesisGraph.setEdgeWeight(synthesisGraph.getEdge(output, function), oldWeight / 5.0);
            }
        }
        return this;
    }

    public CodeSynthesis biasAgainst(Collection<FunctionSignature> signatures) {
        for (FunctionSignature signature : signatures) {
            JavaFunctionNode function = synthesisGraph.lookupFunction(signature.functionName, signature.outputType, signature.argumentTypes);
            if (function != null) {
                System.out.println("Demoting " + function.getName());

                JavaTypeNode output = function.getOutput();
                double oldWeight = synthesisGraph.getWeight(output, function);
                unbiasedWeights.putIfAbsent(function, oldWeight);
                synthesisGraph.setEdgeWeight(synthesisGraph.getEdge(output, function), oldWeight * 5.0);
            }
        }
        return this;
    }

    public CodeSynthesis forbid(Collection<FunctionSignature> signatures) {
        for (FunctionSignature signature : signatures) {
            JavaFunctionNode function = synthesisGraph.lookupFunction(signature.functionName, signature.outputType, signature.argumentTypes);
            if (function != null) {
                System.out.println("Forbidding " + function.getName());

                JavaTypeNode output = function.getOutput();
                double oldWeight = synthesisGraph.getWeight(output, function);
                unbiasedWeights.putIfAbsent(function, oldWeight);
                synthesisGraph.setEdgeWeight(synthesisGraph.getEdge(output, function), Double.POSITIVE_INFINITY);
            }
        }
        return this;
    }

    public CodeSynthesis removeBias() {
        for (Map.Entry<JavaFunctionNode, Double> functionEntry : unbiasedWeights.entrySet()) {
            JavaFunctionNode function = functionEntry.getKey();
            JavaTypeNode output = function.getOutput();
            synthesisGraph.setEdgeWeight(synthesisGraph.getEdge(output, function), functionEntry.getValue());
        }
        return this;
    }

    SortedSet<CodeSnippet> getExpression(JavaGraphNode requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null) return Collections.emptySortedSet();
        if (snippetTable.containsKey(requestedType)) return snippetTable.get(requestedType);

        SortedSet<CodeSnippet> snippets = new TreeSet<>();
        synthTable.get(requestedType)
                .stream()
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
                                new CodeSnippet[choices.size()], nRequested);
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
            int pos, CodeSnippet paramArray[], int nRequested) {
        if (pos == paramArray.length) {
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

    public static class FunctionSignature {
        public String functionName;
        public String outputType;
        public List<String> argumentTypes;

        public FunctionSignature(String functionName, String outputType, String... argumentTypes) {
            this.functionName = functionName;
            this.outputType = outputType;
            this.argumentTypes = Arrays.asList(argumentTypes);
        }
    }

    private static class Generator implements Comparable<Generator> {
        public final JavaGraphNode type;
        public double cost;

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