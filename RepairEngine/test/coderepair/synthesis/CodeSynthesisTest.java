package coderepair.synthesis;

import coderepair.SynthesisGraph;
import coderepair.analysis.JavaGraphNode;
import coderepair.util.GraphLoader;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CodeSynthesisTest {
    private CodeSynthesis synthesis = null;
    final private static String inFile = "./resources/rt.javap";
    final private static String graphFile = "./resources/graph.ser";
    private SynthesisGraph testGraph;

    @Before
    public void setUp() throws Exception {
        testGraph = GraphLoader.getGraph(graphFile, inFile);
        if (testGraph == null) throw new RuntimeException("Could not load graph");

        synthesis = new CodeSynthesis(testGraph);

        testGraph.addLocalVariable("fileName", "java.lang.String");
        testGraph.addLocalVariable("inputText", "java.lang.String");
        testGraph.addLocalVariable("inStream", "java.io.InputStream");
        testGraph.addLocalVariable("outStream", "java.io.OutputStream");
    }

    private void measureBallSize(String type, double costLimit) {
        ClosestFirstIterator<JavaGraphNode, DefaultWeightedEdge> iterator
                = new ClosestFirstIterator<>(testGraph, testGraph.getTypeByName(type), costLimit);

        Set<JavaGraphNode> vertices = new HashSet<>();
        while (iterator.hasNext())
            vertices.add(iterator.next());

        DirectedSubgraph<JavaGraphNode, DefaultWeightedEdge> subgraph
                = new DirectedSubgraph<>(testGraph, vertices, testGraph.edgeSet());

        System.out.printf("(Ball) |V| = %d |E| = %d%n", subgraph.vertexSet().size(), subgraph.edgeSet().size());
        System.out.printf("(Graph) |V| = %d |E| = %d%n", testGraph.vertexSet().size(), testGraph.edgeSet().size());
    }

    private void testSynthesis(String type, String desiredCode, double costLimit, int nRequested) throws InterruptedException {
        System.out.println("\n============= " + type + " =============\n");

        boolean passed = false;
        for (CodeSnippet snippet : synthesis.synthesize(type, costLimit, nRequested)) {
            if (snippet.code.equals(desiredCode)) {
                System.out.print("* ");
                passed = true;
            }
            System.out.printf("%6f  %s%n", snippet.cost, snippet.code);
        }

        System.out.println("\nSizes:");
        measureBallSize(type, costLimit);
        Thread.sleep(10);
        Assert.assertTrue("Synthesis did not return the desired segment", passed);
    }

    @Test
    @BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 5)
    public void testBufferedReader() throws Exception {
        testSynthesis("java.io.BufferedReader", "new BufferedReader(new FileReader(fileName))", 5.0, 10);
    }

    @Test
    @BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 5)
    public void testSequenceInputStream() throws Exception {
        testSynthesis("java.io.SequenceInputStream", "new SequenceInputStream(inStream, System.in)", 5.0, 10);
    }

    @Test
    @BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 5)
    public void testFileInputStream() throws Exception {
        testSynthesis("java.io.FileInputStream", "new FileInputStream(fileName)", 5.0, 10);
    }

    @Test
    @BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 5)
    public void testInputStreamReader() throws Exception {
        testSynthesis("java.io.InputStreamReader", "new InputStreamReader(inStream)", 5.0, 10);
    }

    @Test
    @BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 5)
    public void testMatcher() throws Exception {
        testSynthesis("java.util.regex.Matcher", "(Pattern.compile(fileName)).matcher(inputText)", 5.0, 10);
    }

    @Test
    @BenchmarkOptions(callgc = false, benchmarkRounds = 50, warmupRounds = 5)
    public void testAudioClip() throws Exception {
        testSynthesis("java.applet.AudioClip", "Applet.newAudioClip(new URL(fileName))", 5.0, 10);
    }
}