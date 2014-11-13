package coderepair.util;

import coderepair.GraphBuilder;
import coderepair.SynthesisGraph;
import coderepair.antlr.JavaPLexer;
import coderepair.antlr.JavaPParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Optional;

/**
 * Created by ajr64 on 11/13/14.
 */
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
        return new GraphBuilder(allowedPackages).visitJavap(j);
    }

    private static Optional<SynthesisGraph> loadGraph(String serializedFileName) {
        try (FileInputStream fileInput = new FileInputStream(serializedFileName);
             ObjectInputStream in = new ObjectInputStream(fileInput)) {
            return Optional.of((SynthesisGraph) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static SynthesisGraph getGraph(String serializedFileName, String declarationsFileName, String... allowedPackages) {
        return loadGraph(serializedFileName).orElseGet(() -> (SynthesisGraph)
                        loadFile(declarationsFileName)
                                .flatMap(GraphLoader::parse)
                                .map(j -> GraphLoader.construct(j, allowedPackages))
                                .get()
        );
    }

    public static SynthesisGraph buildGraph(String declarationsFileName, String... allowedPackages) {
        return (SynthesisGraph) loadFile(declarationsFileName)
                .flatMap(GraphLoader::parse)
                .map(j -> GraphLoader.construct(j, allowedPackages))
                .get();
    }

}
