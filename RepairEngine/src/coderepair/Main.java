package coderepair;

import coderepair.analysis.JavaClassType;
import coderepair.analysis.JavaPrimitiveType;
import coderepair.analysis.JavaType;
import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.ext.ComponentAttributeProvider;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String inFile = "/Users/alexreinking/jio.javap";

        System.out.print("Parsing file... ");
        long startTime = System.currentTimeMillis();
        JavaPLexer lexer = new JavaPLexer(new ANTLRFileStream(inFile));
        JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
        GraphBuilder graphBuilder = new GraphBuilder(Arrays.asList("java.io"));
        JavaPParser.JavapContext parsedFile = parser.javap();
        long endTime = System.currentTimeMillis();
        System.out.printf("took %dms\n", endTime - startTime);

        System.out.print("Building graph... ");
        startTime = System.currentTimeMillis();
        SynthesisGraph graph = graphBuilder.visitJavap(parsedFile);
        endTime = System.currentTimeMillis();
        System.out.printf("took %dms\n", endTime - startTime);

        System.out.print("Searching graph... ");
        startTime = System.currentTimeMillis();
        List<DefaultWeightedEdge> path = DijkstraShortestPath.findPathBetween(
                graph, graph.getNodeManager().getTypeFromName("int"),
                graph.getNodeManager().getTypeFromName("java.lang.String"));
        endTime = System.currentTimeMillis();
        System.out.printf("took %dms\n\n", endTime - startTime);

        for (DefaultWeightedEdge edge : path) {
            System.out.printf("(%s) -> (%s)\n", graph.getEdgeSource(edge).getName(), graph.getEdgeTarget(edge).getName());
        }


        IntegerNameProvider<JavaType> idProvider = new IntegerNameProvider<JavaType>();
        VertexNameProvider<JavaType> nameProvider = new VertexNameProvider<JavaType>() {
            @Override public String getVertexName(JavaType type) {
                return type.getName();
            }
        };
        ComponentAttributeProvider<JavaType> colorProvider = new ComponentAttributeProvider<JavaType>() {
            @Override public Map<String, String> getComponentAttributes(JavaType component) {
                if (component instanceof JavaClassType || component instanceof JavaPrimitiveType) {
                    HashMap<String, String> attrMap = new HashMap<String, String>();
                    attrMap.put("fontcolor", "white");
                    attrMap.put("fillcolor", "blue");
                    attrMap.put("shape", "box");
                    attrMap.put("style", "filled");
                    return attrMap;
                }
                return null;
            }
        };

        DOTExporter<JavaType, DefaultWeightedEdge> exporter
                = new DOTExporter<JavaType, DefaultWeightedEdge>(idProvider, nameProvider, null, colorProvider, null);
        exporter.export(new FileWriter("/Users/alexreinking/jio.dot"), graph);
        System.out.println("\nWrote " + graph.vertexSet().size() + " vertices and " + graph.edgeSet().size() + " edges.");
    }
}
