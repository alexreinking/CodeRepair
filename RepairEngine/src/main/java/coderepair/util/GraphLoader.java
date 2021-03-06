package coderepair.util;

import coderepair.antlr4.JavaPLexer;
import coderepair.antlr4.JavaPParser;
import coderepair.graph.*;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GraphLoader {
    @NotNull
    private static Optional<JavaPParser.JavapContext> parseJavap(CharStream stream) {
        JavaPLexer lexer = new JavaPLexer(stream);
        JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
        return Optional.of(parser.javap());
    }

    public static SynthesisGraph fromSerialized(String fileName, String db, String... allowedPackages) {
        return deserializeGraph(fileName).orElseGet(() -> buildGraphFromJavap(db, allowedPackages));
    }

    private static Optional<SynthesisGraph> deserializeGraph(String serializedFileName) {
        try (FileInputStream fileInput = new FileInputStream(serializedFileName);
             ObjectInputStream in = new ObjectInputStream(fileInput)) {
            return Optional.of((SynthesisGraph) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    @NotNull
    public static SynthesisGraph buildGraphFromJavap(String db, String... allowedPackages) {
        return loadFile(db)
                .flatMap(GraphLoader::parseJavap)
                .map(j -> construct(j, allowedPackages))
                .get();
    }

    private static Optional<CharStream> loadFile(String fileName) {
        try {
            return Optional.of(CharStreams.fromFileName(fileName));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static SynthesisGraph construct(JavaPParser.JavapContext j, String... allowedPackages) {
        return new JavapGraphBuilder(allowedPackages).visit(j);
    }

    public static SynthesisGraph fromFunctionList(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            JavaGraphNodeFactory factory = new JavaGraphNodeFactory();
            SynthesisGraph graph = new SynthesisGraph(factory);

            // chomp header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",\\s*");
                double weight = Double.parseDouble(fields[0]);
                JavaTypeNode outputType = factory.getTypeByName(fields[1]);
                String name = fields[2];
                JavaGraphNode.Kind kind = JavaGraphNode.Kind.valueOf(fields[3]);
                String[] sigParts = String.join(", ", Arrays.copyOfRange(fields, 4, fields.length)).split("; ");
                List<JavaTypeNode> signature = new ArrayList<>(sigParts.length);

                graph.addVertex(outputType);
                for (String s : sigParts) {
                    if (s.trim().length() == 0)
                        continue;
                    JavaTypeNode type = factory.getTypeByName(s);
                    signature.add(type);
                    graph.addVertex(type);
                }

                JavaFunctionNode fn = factory.makeFunctionNode(kind, name, signature, outputType);
                graph.addVertex(fn);
                graph.setEdgeWeight(graph.addEdge(outputType, fn), weight);
                for (JavaTypeNode signatureType : signature) {
                    DefaultWeightedEdge e = graph.addEdge(fn, signatureType);
                    if (e != null)
                        graph.setEdgeWeight(e, 0.0);
                }
            }

            return graph;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
