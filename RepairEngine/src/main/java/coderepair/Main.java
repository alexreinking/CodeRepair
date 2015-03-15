package coderepair;

import coderepair.graph.SynthesisGraph;
import coderepair.util.GraphLoader;
import coderepair.util.GraphWriter;
import coderepair.util.TimedTask;
import coderepair.weighting.StochasticWeighting;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String inFile = "./resources/rt.javap";
        final String graphFile = "./resources/graph.ser";
        final String csvInFile = "./resources/graph.freq.csv";
        final String csvOutFile = "./resources/graph.csv";

//        final SynthesisGraph graph = GraphLoader.fromSerialized(graphFile, inFile, "java.io", "java.util.regex", "java.applet", "javax.swing", "java.net");
        final SynthesisGraph graph = GraphLoader.fromFunctionList(csvInFile);

        TimedTask weightGraph = new TimedTask("Graph weighting", () -> {
            System.out.println("Weighting graph on " + graph.vertexSet().size() + " vertices.");
//            new FrequencyWeighting(f -> f).applyWeight(graph);
            new StochasticWeighting().applyWeight(graph);
        });

        TimedTask serializeGraph = new TimedTask("Graph serialization", () -> {
            GraphWriter.exportSerialized(graph, graphFile);
            GraphWriter.exportTabular(graph, csvOutFile);
        });

        weightGraph.andThen(serializeGraph).run();
        System.out.println("Done");
    }
}
