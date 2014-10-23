package coderepair.sourcescanner;

import coderepair.SynthesisGraph;
import coderepair.analysis.JavaGraphNode;
import coderepair.analysis.JavaTypeNode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by alex on 10/22/14.
 */
public class SourceScanner {
    private static final String graphFile = "./data/graph.ser";

    public static void main(String[] args) {
        try {
            FileInputStream fileInput = new FileInputStream(graphFile);
            ObjectInputStream in = new ObjectInputStream(fileInput);
            SynthesisGraph graph = (SynthesisGraph) in.readObject();
            in.close();
            fileInput.close();

            for (JavaGraphNode javaGraphNode : graph.vertexSet()) {
                if (javaGraphNode instanceof JavaTypeNode) {
                    JavaTypeNode typeNode = (JavaTypeNode) javaGraphNode;
                    System.out.println(typeNode.getPackageName() + ":::" + typeNode.getClassName());
                }
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
