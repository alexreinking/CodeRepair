package coderepair.util;

import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import coderepair.graph.JavapGraphBuilder;
import coderepair.graph.SynthesisGraph;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Optional;

public class GraphLoader {
    private static Optional<ANTLRFileStream> loadFile(String fileName) {
        try {
            return Optional.of(new ANTLRFileStream(fileName));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static Optional<JavaPParser.JavapContext> parse(ANTLRFileStream antlrFileStream) {
        JavaPLexer lexer = new JavaPLexer(antlrFileStream);
        JavaPParser parser = new JavaPParser(new BufferedTokenStream(lexer));
        return Optional.of(parser.javap());
    }

    private static SynthesisGraph construct(JavaPParser.JavapContext j, String... allowedPackages) {
        return new JavapGraphBuilder(allowedPackages).visitJavap(j);
    }

    private static Optional<SynthesisGraph> loadGraph(String serializedFileName) {
        try (FileInputStream fileInput = new FileInputStream(serializedFileName);
             ObjectInputStream in = new ObjectInputStream(fileInput)) {
            return Optional.of((SynthesisGraph) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static SynthesisGraph getGraph(String serializedFileName, String declarationsFileName, String... allowedPackages) {
        return loadGraph(serializedFileName).orElseGet(() -> buildGraph(declarationsFileName, allowedPackages));
    }

    public static SynthesisGraph buildGraph(String declarationsFileName, String... allowedPackages) {
        return loadFile(declarationsFileName)
                .flatMap(GraphLoader::parse)
                .map(j -> construct(j, allowedPackages))
                .get();
    }

}
