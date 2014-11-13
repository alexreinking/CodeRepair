package coderepair;

import coderepair.util.GraphLoader;
import coderepair.util.TimedTask;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "./resources/rt.javap";
        final String graphFile = "./resources/graph.ser";

        final SynthesisGraph graph = GraphLoader.getGraph(graphFile, inFile);

        TimedTask serializeGraph = new TimedTask("Graph serialization", () -> {
            try (FileOutputStream fileOut = new FileOutputStream(graphFile)) {
                graph.serialize(fileOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        serializeGraph.run();
    }
}
