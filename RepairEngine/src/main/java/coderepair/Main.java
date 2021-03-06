package coderepair;

import coderepair.graph.SynthesisGraph;
import coderepair.util.GraphLoader;
import coderepair.util.GraphWriter;
import coderepair.util.TimedTask;
import coderepair.weighting.NaiveWeighting;

public class Main {
    public static void main(String[] args) {
        final String inFile = "./resources/rt.javap";
        final String graphFile = "./resources/graph.ser";
        final String csvInFile = "./resources/graph.freq.csv";
        final String csvOutFile = "./resources/graph.csv";

        final SynthesisGraph graph = GraphLoader.fromSerialized(
                graphFile,
                inFile,
                "java.io",
                "java.util",
                "java.applet",
                "javax.swing",
                "java.net",
                "java.awt",
                "javax.xml",
                "org.w3c",
                "org.xml"
        );
//        final SynthesisGraph graph = GraphLoader.fromFunctionList(csvInFile);

        TimedTask weightGraph = new TimedTask("Graph weighting", () -> {
            System.out.println("Weighting graph on " + graph.vertexSet().size() + " vertices.");
//            new FrequencyWeighting(f -> f).applyWeight(graph);
//            new StochasticWeighting().applyWeight(graph);
            new NaiveWeighting().applyWeight(graph);
        });

        TimedTask serializeGraph = new TimedTask("Graph serialization", () -> {
            GraphWriter.exportSerialized(graph, graphFile);
            GraphWriter.exportTabular(graph, csvOutFile);
        });

        weightGraph.andThen(serializeGraph).run();
        System.out.println("Done");
    }
}
