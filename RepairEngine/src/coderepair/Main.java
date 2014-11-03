package coderepair;

import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.synthesis.CodeSnippet;
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

        TimedTask parseInput = new TimedTask("Parse", new Runnable() {
            @Override
            public void run() {
                try {
                    JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(inFile));
                    JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
                    graphBuilder[0] = new GraphBuilder(10.0);
                    parseTree[0] = parser.javap();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TimedTask buildGraph = new TimedTask("Graph construction", new Runnable() {
            @Override
            public void run() {
                graph[0] = graphBuilder[0].visitJavap(parseTree[0]);
            }
        });

        TimedTask serializeGraph = new TimedTask("Graph serialization", new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fileOut = new FileOutputStream(graphFile);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(graph[0]);
                    out.close();
                    fileOut.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        TimedTask loadGraph = new TimedTask("Graph deserialization", new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fileInput = new FileInputStream(graphFile);
                    ObjectInputStream in = new ObjectInputStream(fileInput);
                    graph[0] = (SynthesisGraph) in.readObject();
                    in.close();
                    fileInput.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        TimedTask synthesize = new TimedTask("Synthesis", new Runnable() {
            @Override
            public void run() {
                graph[0].resetLocals();
                graph[0].addLocalVariable("fileName", "java.lang.String");
                graph[0].addLocalVariable("inputText", "java.lang.String");
                graph[0].addLocalVariable("inStream", "java.io.InputStream");
                graph[0].addLocalVariable("outStream", "java.io.InputStream");

                for (String cls : Arrays.asList("java.io.SequenceInputStream", "java.io.BufferedReader",
                        "java.io.FileInputStream", "java.io.InputStreamReader", "java.util.regex.Matcher")) {
                    System.out.println("\n============= " + cls + " =============\n");
                    for (CodeSnippet snippet : graph[0].synthesize(cls, 10))
                        System.out.printf("%6f  %s\n", snippet.cost, snippet.code);
                }
            }
        });

        loadGraph.orElse(parseInput.andThen(buildGraph).andThen(serializeGraph)).andThen(synthesize).run();
    }
}
