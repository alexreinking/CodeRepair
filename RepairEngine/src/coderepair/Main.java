package coderepair;

import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.util.TimedTask;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

import static coderepair.SynthesisGraph.Snippet;

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
                    graphBuilder[0] = new GraphBuilder(Arrays.asList("java.io", "java.nio"), 5.0);
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
                TreeSet<Snippet> snippets = graph[0].synthesize("java.io.BufferedReader", 10);
                for (Snippet snippet : snippets) System.out.println(snippet.code + " == " + snippet.cost);
            }
        });

        parseInput.run();
        buildGraph.run();
        synthesis.run();
    }
}
