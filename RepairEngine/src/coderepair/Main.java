package coderepair;

import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.CodeSynthesis;
import coderepair.util.TimedTask;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "./data/rt.javap";
        final String graphFile = "./data/graph.ser";
        final JavaPParser.JavapContext[] parseTree = new JavaPParser.JavapContext[1];
        final SynthesisGraph[] graph = new SynthesisGraph[1];
        final GraphBuilder[] graphBuilder = new GraphBuilder[1];

        TimedTask parseInput = new TimedTask("Parse", () -> {
            try {
                JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(inFile));
                JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
                graphBuilder[0] = new GraphBuilder();
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
            graph[0].resetLocals();
            graph[0].addLocalVariable("fileName", "java.lang.String");
            graph[0].addLocalVariable("inputText", "java.lang.String");
            graph[0].addLocalVariable("inStream", "java.io.InputStream");
            graph[0].addLocalVariable("outStream", "java.io.InputStream");

            for (String cls : Arrays.asList("java.io.BufferedReader")) {
                System.out.println("\n============= " + cls + " =============\n");
                CodeSynthesis synthesis = new CodeSynthesis(graph[0]);

                for (CodeSnippet snippet : synthesis.synthesize(cls, 5.0, 10))
                    System.out.printf("%6f  %s%n", snippet.cost, snippet.code);
            }
        });

        loadGraph.orElse(parseInput.andThen(buildGraph).andThen(serializeGraph)).andThen(synthesize).run();
    }
}
