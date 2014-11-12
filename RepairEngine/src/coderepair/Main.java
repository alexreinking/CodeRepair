package coderepair;

import coderepair.analysis.JavaTypeNode;
import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.CodeSynthesis;
import coderepair.util.TimedTask;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

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
            for (String type : Arrays.asList("java.io.SequenceInputStream", "java.io.BufferedReader",
                    "java.io.FileInputStream", "java.io.InputStreamReader", "java.util.regex.Matcher")) {
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
            SynthesisGraph synthesisGraph = graph[0];
            synthesisGraph.addLocalVariable("fileName", "java.lang.String");

            CodeSynthesis synthesis = new CodeSynthesis(synthesisGraph);

            Optional<CodeSnippet> bestSnippetOpt;
            CodeSnippet bestSnippet;

            /* Stage 1 */
            bestSnippetOpt = synthesis.synthesize("java.io.FileInputStream", 7.0, 10)
                    .stream().min((snip1, snip2) -> Double.compare(snip1.cost, snip2.cost));

            bestSnippet = bestSnippetOpt.get();
            System.out.printf("* %6f  %s%n", bestSnippet.cost, bestSnippet.code);
            synthesis.strongEnforce("java.io.FileInputStream", new CodeSnippet(bestSnippet.code, 0.0));

            /* Stage 2 */
            synthesis.strongEnforce("boolean", new CodeSnippet("true", 0.0));
            synthesis.strongEnforce("int", new CodeSnippet("compLevel", 0.0));

            bestSnippetOpt = synthesis.synthesize("java.util.zip.DeflaterInputStream", 7.0, 10)
                    .stream().min((snip1, snip2) -> Double.compare(snip1.cost, snip2.cost));

            bestSnippet = bestSnippetOpt.get();
            System.out.printf("* %6f  %s%n", bestSnippet.cost, bestSnippet.code);
            synthesis.strongEnforce("java.util.zip.DeflaterInputStream", new CodeSnippet(bestSnippet.code, 0.0));

            /* Stage 3 */
            synthesis.strongEnforce("int", new CodeSnippet("buffSize", 0.0));

            bestSnippetOpt = synthesis.synthesize("java.io.BufferedInputStream", 7.0, 10)
                    .stream().min((snip1, snip2) -> Double.compare(snip1.cost, snip2.cost));

            bestSnippet = bestSnippetOpt.get();
            System.out.printf("* %6f  %s%n", bestSnippet.cost, bestSnippet.code);
            synthesis.strongEnforce("java.io.BufferedInputStream", new CodeSnippet(bestSnippet.code, 0.0));
        });

        loadGraph.orElse(parseInput.andThen(buildGraph).andThen(serializeGraph)).andThen(simulatedRepair.times(20)).run();
    }
}
