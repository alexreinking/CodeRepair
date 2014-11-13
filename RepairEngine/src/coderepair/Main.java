package coderepair;

import coderepair.analysis.JavaGraphNode;
import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.CodeSynthesis;
import coderepair.util.TimedTask;
import com.intellij.util.Producer;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.traverse.ClosestFirstIterator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "./resources/rt.javap";
        final String graphFile = "./resources/graph.ser";
        final JavaPParser.JavapContext[] parseTree = new JavaPParser.JavapContext[1];
        final SynthesisGraph[] graph = new SynthesisGraph[1];
        final GraphBuilder[] graphBuilder = new GraphBuilder[1];

        TimedTask parseInput = new TimedTask("Parse", () -> {
            try {
                JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(inFile));
                JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
                graphBuilder[0] = new GraphBuilder(Arrays.asList("java"));
                parseTree[0] = parser.javap();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        TimedTask buildGraph = new TimedTask("Graph construction",
                () -> graph[0] = graphBuilder[0].visitJavap(parseTree[0]));

        TimedTask serializeGraph = new TimedTask("Graph serialization", () -> {
            try {
                FileOutputStream fileOut = new FileOutputStream(graphFile);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(graph[0]);
                out.close();
                fileOut.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        TimedTask loadGraph = new TimedTask("Graph deserialization", () -> {
            try {
                FileInputStream fileInput = new FileInputStream(graphFile);
                ObjectInputStream in = new ObjectInputStream(fileInput);
                graph[0] = (SynthesisGraph) in.readObject();
                in.close();
                fileInput.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        TimedTask synthesize = new TimedTask("Synthesis", () -> {
            SynthesisGraph synthesisGraph = graph[0];
            synthesisGraph.resetLocals();
            for (String type : Arrays.asList("java.io.SequenceInputStream", "java.io.BufferedReader",
                    "java.io.FileInputStream", "java.io.InputStreamReader", "java.util.regex.Matcher",
                    "java.applet.AudioClip")) {
                System.out.println("\n============= " + type + " =============\n");

                CodeSynthesis synthesis = new CodeSynthesis(synthesisGraph);
                synthesisGraph.addLocalVariable("fileName", "java.lang.String");
                synthesisGraph.addLocalVariable("inputText", "java.lang.String");
                synthesisGraph.addLocalVariable("inStream", "java.io.InputStream");
                synthesisGraph.addLocalVariable("outStream", "java.io.InputStream");

                for (CodeSnippet snippet : synthesis.synthesize(type, 5.0, 10))
                    System.out.printf("%6f  %s%n", snippet.cost, snippet.code);
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

        loadGraph.orElse(parseInput.andThen(buildGraph).andThen(serializeGraph))
//                .andThen(synthesize)
                .andThen(simulatedRepair)
//                .andThen(ballGrowth)
                .run();
    }
}
