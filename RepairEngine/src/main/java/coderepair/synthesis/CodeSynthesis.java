package coderepair.synthesis;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaGraphNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.ExpressionTree;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.ClosestFirstIterator;

import java.util.*;
import java.util.stream.Collectors;

public class CodeSynthesis {
    private final SynthesisGraph synthesisGraph;
    private final ExpressionTreeBuilder builder;
    private final Map<JavaTypeNode, List<JavaFunctionNode>> synthTable = new HashMap<>();
    private final Map<JavaTypeNode, SortedSet<ExpressionTree>> snippetTable = new HashMap<>();
    private final Map<JavaTypeNode, SortedSet<ExpressionTree>> enforcedSnippets = new HashMap<>();
    private final Set<ExpressionTree> allEnforced = new HashSet<>();

    public CodeSynthesis(SynthesisGraph synthesisGraph, ExpressionTreeBuilder builder) {
        this.synthesisGraph = synthesisGraph;
        this.builder = builder;
    }

    public SortedSet<ExpressionTree> synthesize(String qualifiedName, double targetConductance, int nRequested) {
        synthTable.clear();
        snippetTable.clear();

        if (!synthesisGraph.hasType(qualifiedName)) return Collections.emptySortedSet();

        JavaGraphNode requestedType = synthesisGraph.getTypeByName(qualifiedName);
        if (requestedType == null) return Collections.emptySortedSet();
        Stack<JavaTypeNode> typesByDistance = new Stack<>();

        ClosestFirstIterator<JavaGraphNode, DefaultWeightedEdge> costLimitBall = new ClosestFirstIterator<>(synthesisGraph, requestedType);

        Set<JavaGraphNode> cut = new HashSet<>();
        double totalEdges = 0;
        double edgesInside = 0;
        double conductance;
        double costLimit;

        do {
            JavaGraphNode next = costLimitBall.next();

            for (JavaGraphNode nbr : Graphs.successorListOf(synthesisGraph, next)) {
                totalEdges += synthesisGraph.getWeight(next, nbr);
                if (cut.contains(nbr))
                    edgesInside += synthesisGraph.getWeight(next, nbr);
            }

            for (JavaGraphNode nbr : Graphs.predecessorListOf(synthesisGraph, next)) {
                totalEdges += synthesisGraph.getWeight(nbr, next);
                if (cut.contains(nbr))
                    edgesInside += synthesisGraph.getWeight(nbr, next);
            }

            cut.add(next);
            conductance = (totalEdges - 2 * edgesInside) / totalEdges;

            if (next instanceof JavaFunctionNode) {
                JavaFunctionNode fn = (JavaFunctionNode) next;
                JavaTypeNode output = fn.getOutput();
                synthTable.computeIfAbsent(output, t -> new ArrayList<>()).add(fn);
            } else typesByDistance.push((JavaTypeNode) next);

            costLimit = costLimitBall.getShortestPathLength(next);

            if (cut.size() > 500 && conductance < targetConductance)
                break;
        } while (costLimitBall.hasNext() && cut.size() < synthesisGraph.vertexSet().size() / 2);

        System.out.printf("dynamically chosen cost limit = %s (%d vertices)%n", costLimit, cut.size());

        while (!typesByDistance.empty()) {
            JavaTypeNode top = typesByDistance.pop();
            double costDeficit = costLimitBall.getShortestPathLength(top);
            double effectiveCostLimit = costLimit - costDeficit;
            snippetTable.put(top, getExpression(top, effectiveCostLimit, nRequested));
        }

        snippetTable.keySet().stream().filter(k -> snippetTable.get(k).isEmpty()).collect(Collectors.toSet()).forEach(snippetTable::remove);
        return snippetTable.getOrDefault(requestedType, Collections.emptySortedSet());
    }

    public void strongEnforce(String type, ExpressionTree snippet) {
        for (JavaTypeNode assignableType : synthesisGraph.getAssignableTypes(synthesisGraph.getTypeByName(type)))
            enforce(assignableType.getName(), snippet);
    }

    public void enforce(String type, ExpressionTree snippet) {
        enforcedSnippets.computeIfAbsent(synthesisGraph.getTypeByName(type), v -> new TreeSet<>()).add(snippet);
        allEnforced.add(snippet);
    }

    public void relax() {
        enforcedSnippets.clear();
        allEnforced.clear();
    }

    SortedSet<ExpressionTree> getExpression(JavaTypeNode requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null) return Collections.emptySortedSet();
        if (snippetTable.containsKey(requestedType)) return snippetTable.get(requestedType);

        SortedSet<ExpressionTree> snippets = Collections.synchronizedSortedSet(new BoundedSortedSet<>(nRequested));
        snippets.addAll(enforcedSnippets.getOrDefault(requestedType, Collections.emptySortedSet()));
        synthTable.get(requestedType)
                .stream()
                .forEach(funcGen -> {
                    double nextCost = remaining - synthesisGraph.getWeight(requestedType, funcGen);
                    if (nextCost < 0.0)
                        return;

                    List<SortedSet<ExpressionTree>> choices =
                            funcGen.getSignature()
                                    .stream()
                                    .map(input -> getExpression(input, nextCost, nRequested))
                                    .filter(resultSet -> !resultSet.isEmpty())
                                    .collect(Collectors.toList());

                    if (choices.size() == funcGen.getSignature().size())
                        addFunctionPossibilities(snippets, funcGen, choices, 0, new ExpressionTree[choices.size()]);
                });

        return snippets;
    }

    void addFunctionPossibilities(
            SortedSet<ExpressionTree> snippets, JavaFunctionNode functionType,
            List<SortedSet<ExpressionTree>> synths,
            int pos, ExpressionTree[] args) {
        if (pos == args.length) {
            snippets.add(builder.buildInvocation(functionType, args));
        } else for (ExpressionTree subExpr : synths.get(pos)) {
            args[pos] = subExpr;
            addFunctionPossibilities(snippets, functionType, synths, pos + 1, args);
        }
    }
}
