package coderepair.synthesis;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaGraphNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.ExpressionTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.ClosestFirstIterator;

import java.util.*;
import java.util.stream.Collectors;

public class CodeSynthesis {
    private final SynthesisGraph synthesisGraph;
    private final ExpressionTreeBuilder builder;
    private final Map<JavaTypeNode, List<Generator>> synthTable = new HashMap<>();
    private final Map<JavaTypeNode, SortedSet<ExpressionTree>> snippetTable = new HashMap<>();

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

        ClosestFirstIterator<JavaGraphNode, DefaultWeightedEdge> neighborhood = new ClosestFirstIterator<>(synthesisGraph, requestedType);

        Set<JavaGraphNode> cut = new HashSet<>();
        double totalEdges = 0;
        double edgesInside = 0;
        double conductance;
        double costLimit;

        do {
            JavaGraphNode next = neighborhood.next();

            for (DefaultWeightedEdge edge : synthesisGraph.edgesOf(next)) {
                JavaGraphNode neighbor = (synthesisGraph.getEdgeSource(edge).equals(next))
                        ? synthesisGraph.getEdgeTarget(edge)
                        : synthesisGraph.getEdgeSource(edge);

                double wgt = 1 + synthesisGraph.getEdgeWeight(edge);
                totalEdges += wgt;
                if (cut.contains(neighbor))
                    edgesInside += wgt;
            }

            cut.add(next);
            conductance = (totalEdges - 2 * edgesInside) / totalEdges;

            if (next.getKind().equals(JavaGraphNode.Kind.Type))
                typesByDistance.push((JavaTypeNode) next);
            else {
                JavaFunctionNode fn = (JavaFunctionNode) next;
                synthTable.computeIfAbsent(fn.getOutput(), t -> new ArrayList<>()).add(new Generator(fn, synthesisGraph.getWeight(fn.getOutput(), fn)));
            }

            costLimit = neighborhood.getShortestPathLength(next);

            if (cut.size() > 500 && conductance < targetConductance)
                break;
        } while (neighborhood.hasNext() && cut.size() < synthesisGraph.vertexSet().size() / 2);

        System.out.printf("dynamically chosen cost limit = %s (%d vertices)%n", costLimit, cut.size());

        while (!typesByDistance.empty()) {
            JavaTypeNode top = typesByDistance.pop();
            double costDeficit = neighborhood.getShortestPathLength(top);
            double effectiveCostLimit = costLimit - costDeficit;
            snippetTable.put(top, getExpression(top, effectiveCostLimit, nRequested));
        }

        snippetTable.keySet().stream().filter(k -> snippetTable.get(k).isEmpty()).collect(Collectors.toSet()).forEach(snippetTable::remove);
        return snippetTable.getOrDefault(requestedType, Collections.emptySortedSet());
    }

    SortedSet<ExpressionTree> getExpression(JavaTypeNode requestedType, double remaining, int nRequested) {
        if (synthTable.get(requestedType) == null) return Collections.emptySortedSet();
        if (snippetTable.containsKey(requestedType)) return snippetTable.get(requestedType);

        SortedSet<ExpressionTree> snippets = Collections.synchronizedSortedSet(new BoundedSortedSet<>(nRequested, builder.getComparator()));
        synthTable.get(requestedType)
                .parallelStream()
                .filter(g -> remaining - g.cost > 0)
                .forEach(gen -> {
                    JavaFunctionNode funcGen = gen.node;

                    List<SortedSet<ExpressionTree>> choices = new ArrayList<>(funcGen.getTotalFormals());
                    for (JavaTypeNode input : funcGen.getSignature()) {
                        SortedSet<ExpressionTree> arg = getExpression(input, remaining - gen.cost, nRequested);
                        if (arg.size() == 0)
                            return;
                        choices.add(arg);
                    }

                    if (choices.size() == funcGen.getSignature().size())
                        addFunctionPossibilities(snippets, funcGen, choices, 0, new ExpressionTree[choices.size()]);
                });
        return snippets;
    }

    void addFunctionPossibilities(SortedSet<ExpressionTree> snippets, JavaFunctionNode functionType,
                                  List<SortedSet<ExpressionTree>> synths, int pos, ExpressionTree[] args) {
        if (pos == args.length) {
            snippets.add(builder.buildInvocation(functionType, args));
        } else for (ExpressionTree subExpr : synths.get(pos)) {
            args[pos] = subExpr;
            addFunctionPossibilities(snippets, functionType, synths, pos + 1, args);
        }
    }

    private class Generator {
        public JavaFunctionNode node;
        public double cost;

        public Generator(JavaFunctionNode node, double cost) {
            this.cost = cost;
            this.node = node;
        }
    }
}
