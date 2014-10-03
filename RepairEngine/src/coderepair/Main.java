package coderepair;

import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
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

        TimedTask parseInput = new TimedTask("Parsing", new Runnable() {
            @Override public void run() {
                try {
                    JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(inFile));
                    JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
                    graphBuilder[0] = new GraphBuilder(Arrays.asList("java.io"));
                    parseTree[0] = parser.javap();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TimedTask buildGraph = new TimedTask("Building Graph", new Runnable() {
            @Override public void run() {
                graph[0] = graphBuilder[0].visitJavap(parseTree[0]);
            }
        });

        TimedTask synthesis = new TimedTask("Synthesizing int", new Runnable() {
            @Override public void run() {
                graph[0].synthesizeType("int");
            }
        });

        TimedTask writeDot = new TimedTask("Writing dot file", new Runnable() {
            @Override public void run() {
                try {
                    graph[0].exportToFile(new FileWriter("/Users/alexreinking/jio.dot"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        parseInput.run();
        buildGraph.run(10);
        synthesis.run();
        writeDot.run();
    }

    static private class TimedTask {
        private final String taskName;
        private final Runnable task;

        public TimedTask(String taskName, Runnable task) {
            this.taskName = taskName;
            this.task = task;
        }

        public void run() {
            run(1);
        }

        public void run(int nTrials) {
            System.out.printf("%s... ", taskName);
            long bestTime = Long.MAX_VALUE;
            for (int i = 0; i < nTrials; i++) {
                long startTime = System.currentTimeMillis();
                task.run();
                long stopTime = System.currentTimeMillis();
                if (stopTime - startTime < bestTime)
                    bestTime = stopTime - startTime;
            }
            System.out.printf("took %dms (best of %d)\n", bestTime, nTrials);
        }
    }
}
