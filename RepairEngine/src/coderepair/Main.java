package coderepair;

import coderepair.analysis.JavaGraphNode;
import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.CodeSynthesis;
import coderepair.util.GraphLoader;
import coderepair.util.TimedTask;
import com.intellij.util.Producer;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.traverse.ClosestFirstIterator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "./resources/rt.javap";
        final String graphFile = "./resources/graph.ser";
        final SynthesisGraph[] graph = new SynthesisGraph[] {
            GraphLoader.getGraph(graphFile, inFile, "java.io", "java.util", "java.net", "java.applet")
        };

        TimedTask serializeGraph = new TimedTask("Graph serialization", () -> {
            try (FileOutputStream fileOut = new FileOutputStream(graphFile)) {
                graph[0].serialize(fileOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        TimedTask simulatedRepair = new TimedTask("Repair-ish", () -> {
            System.out.println();

            SynthesisGraph synthesisGraph = graph[0];
            synthesisGraph.resetLocals();
            synthesisGraph.addLocalVariable("fileName", "java.lang.String");

            CodeSynthesis synthesis = new CodeSynthesis(synthesisGraph);
            CodeSnippet bestSnippet;

            Producer<Double> p = () -> 0.01 * Math.random();

            /* Stage 1 */
            bestSnippet = synthesis.synthesize("java.io.FileInputStream", 6.0, 10).first();
            System.out.printf("* %6f  %s%n", bestSnippet.cost, bestSnippet.code);
            synthesis.strongEnforce("java.io.FileInputStream", new CodeSnippet(bestSnippet.code, p.produce()));

            /* Stage 2 */
            synthesis.strongEnforce("boolean", new CodeSnippet("true", p.produce()));
            synthesis.strongEnforce("int", new CodeSnippet("compLevel", p.produce()));

            bestSnippet = synthesis.synthesize("java.util.zip.DeflaterInputStream", 6.0, 10).first();
            System.out.printf("* %6f  %s%n", bestSnippet.cost, bestSnippet.code);
            synthesis.strongEnforce("java.util.zip.DeflaterInputStream", new CodeSnippet(bestSnippet.code, p.produce()));

            /* Stage 3 */
            synthesis.strongEnforce("int", new CodeSnippet("buffSize", 0.1 * Math.random()));

            bestSnippet = synthesis.synthesize("java.io.BufferedInputStream", 6.0, 10).first();
            System.out.printf("* %6f  %s%n", bestSnippet.cost, bestSnippet.code);
            synthesis.strongEnforce("java.io.BufferedInputStream", new CodeSnippet(bestSnippet.code, p.produce()));
        });

        TimedTask ballGrowth = new TimedTask("Export", () -> {
            System.out.println();

            ClosestFirstIterator<JavaGraphNode, DefaultWeightedEdge> iterator
                    = new ClosestFirstIterator<>(graph[0], graph[0].getTypeByName("java.io.BufferedReader"), 5.0);


            Set<JavaGraphNode> vertices = new HashSet<>();
            while (iterator.hasNext())
                vertices.add(iterator.next());

            DirectedSubgraph<JavaGraphNode, DefaultWeightedEdge> subgraph
                    = new DirectedSubgraph<>(graph[0], vertices, graph[0].edgeSet());

            System.out.printf("(Ball) |V| = %d |E| = %d%n", subgraph.vertexSet().size(), subgraph.edgeSet().size());
            System.out.printf("(Graph) |V| = %d |E| = %d%n", graph[0].vertexSet().size(), graph[0].edgeSet().size());
            try {
                SynthesisGraph.exportToFile(Files.newBufferedWriter(Paths.get("./resources/graph.dot")), subgraph);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serializeGraph.andThen(simulatedRepair.times(50))
//                .andThen(simulatedRepair.times(50))
                .andThen(ballGrowth)
                .run();
    }
}
