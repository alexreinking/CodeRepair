package coderepair.util;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaGraphNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Created by alexreinking on 3/12/15.
 */
public class GraphWriter {
    public static void exportTabular(SynthesisGraph graph, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write("cost, return, name, type, signature");
            bw.newLine();
            graph.vertexSet().stream().filter(v -> v instanceof JavaFunctionNode).forEach(node -> {
                try {
                    JavaFunctionNode fn = (JavaFunctionNode) node;
                    JavaTypeNode output = fn.getOutput();

                    String sigStr = fn.getSignature().stream().map(JavaGraphNode::getName).collect(Collectors.joining("; "));

                    bw.write(String.format("%.8f, %s, %s, %s, %s", graph.getWeight(output, fn), output.getName(), fn.getFunctionName(), fn.getKind().name(), sigStr));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exportSerialized(SynthesisGraph graph, String fileName) {
        try (FileOutputStream outputStream = new FileOutputStream(fileName);
             ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
            out.writeObject(graph);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
