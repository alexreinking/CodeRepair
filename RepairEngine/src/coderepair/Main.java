package coderepair;

import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.synthesis.CodeSnippet;
import coderepair.util.TimedTask;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "./data/rt.javap";
        final JavaPParser.JavapContext[] parseTree = new JavaPParser.JavapContext[1];
        final SynthesisGraph[] graph = new SynthesisGraph[1];
        final GraphBuilder[] graphBuilder = new GraphBuilder[1];

        TimedTask parseInput = new TimedTask("Parse", new Runnable() {
            @Override public void run() {
                try {
                    JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(inFile));
                    JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
                    graphBuilder[0] = new GraphBuilder(Arrays.asList("java.io", "java.nio"), 10.0);
                    parseTree[0] = parser.javap();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TimedTask buildGraph = new TimedTask("Graph construction", new Runnable() {
            @Override public void run() {
                graph[0] = graphBuilder[0].visitJavap(parseTree[0]);
            }
        });

        TimedTask synthesis = new TimedTask("Synthesis", new Runnable() {
            @Override public void run() {
                graph[0].resetLocals();
                graph[0].addLocalVariable("body", "java.lang.String");
                graph[0].addLocalVariable("sig", "java.lang.String");
                graph[0].addLocalVariable("inStream", "java.io.InputStream");
                graph[0].addLocalVariable("outStream", "java.io.InputStream");

                for (String cls : Arrays.asList("java.io.SequenceInputStream", "java.io.BufferedReader",
                        "java.io.FileInputStream", "java.io.InputStreamReader")) {
                    System.out.println("\n============= " + cls + " =============\n");
                    for (CodeSnippet snippet : graph[0].synthesize(cls, 10))
                        System.out.printf("%6f  %s\n", snippet.cost, snippet.code);
                }
            }
        });

        parseInput.run();
        buildGraph.run();
        synthesis.run();
    }
}
