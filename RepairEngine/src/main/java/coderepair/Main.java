package coderepair;

import coderepair.util.GraphLoader;
import coderepair.util.TimedTask;
import coderepair.weighting.NaiveWeighting;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "./resources/rt.javap";
        final String graphFile = "./resources/graph.ser";

        final SynthesisGraph graph = GraphLoader.getGraph(graphFile, inFile, "java.io", "java.util.regex", "java.applet", "javax.swing", "java.net");

        TimedTask weightGraph = new TimedTask("Graph weighting", () -> {
            System.out.println("Weighting graph on " + graph.vertexSet().size() + " vertices.");
//            new StochasticWeighting(f -> 1.0 / Math.log(f)).applyWeight(graph);
            new NaiveWeighting().applyWeight(graph);
        });

        TimedTask serializeGraph = new TimedTask("Graph serialization", () -> {
            try (FileOutputStream fileOut = new FileOutputStream(graphFile)) {
                graph.serialize(fileOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        weightGraph.andThen(serializeGraph).run();
        System.out.println("Done");
    }
}
