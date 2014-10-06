package coderepair;

import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.util.TimedTask;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "/Users/alexreinking/jio.javap";
        final JavaPParser.JavapContext[] parseTree = new JavaPParser.JavapContext[1];
        final SynthesisGraph[] graph = new SynthesisGraph[1];
        final GraphBuilder[] graphBuilder = new GraphBuilder[1];

        TimedTask parseInput = new TimedTask("Parse", new Runnable() {
            @Override public void run() {
                try {
                    JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(inFile));
                    JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
                    graphBuilder[0] = new GraphBuilder(Arrays.asList("java.io", "java.nio"));
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
                graph[0].synthesizeType("byte[]");
            }
        });

        TimedTask writeDot = new TimedTask("Writing GraphViz output", new Runnable() {
            @Override public void run() {
                try {
                    graph[0].exportToFile(new FileWriter("/Users/alexreinking/jio.dot"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        parseInput.run();
        buildGraph.run();
        synthesis.run();
        writeDot.run(0);
    }
}
