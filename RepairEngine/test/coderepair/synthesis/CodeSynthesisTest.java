package coderepair.synthesis;

import coderepair.SynthesisGraph;
import coderepair.analysis.JavaGraphNode;
import coderepair.util.GraphLoader;
import coderepair.util.TimedTask;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CodeSynthesisTest {
    private static final double COST_LIMIT = 4.5;
    private static final int REQUESTED = 10;
    private static final int N_TRIALS = 1;
    private CodeSynthesis synthesis = null;
    final private static String inFile = "./resources/rt.javap";
    final private static String graphFile = "./resources/graph.ser";
    private SynthesisGraph testGraph;

    @Before
    public void setUp() throws Exception {
        if (testGraph == null) {
            testGraph = GraphLoader.getGraph(graphFile, inFile);
            if (testGraph == null) throw new RuntimeException("Could not load graph");

            synthesis = new CodeSynthesis(testGraph);

            testGraph.resetLocals();
            testGraph.addLocalVariable("fileName", "java.lang.String");
            testGraph.addLocalVariable("inputText", "java.lang.String");
            testGraph.addLocalVariable("inStream1", "java.io.InputStream");
            testGraph.addLocalVariable("inStream2", "java.io.InputStream");
            testGraph.addLocalVariable("outStream", "java.io.OutputStream");
        }
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

    private void testSynthesis(String type, String desiredCode) throws InterruptedException {
        final boolean[] passed = {false};

        new TimedTask("Synthesis", () -> {
            System.out.println("\n============= " + type + " =============\n");
            for (CodeSnippet snippet : synthesis.synthesize(type,
                    CodeSynthesisTest.COST_LIMIT, CodeSynthesisTest.REQUESTED)) {
                if (snippet.code.equals(desiredCode)) {
                    System.out.print("* ");
                    passed[0] = true;
                }
                System.out.printf("%6f  %s%n", snippet.cost, snippet.code);
            }
        }).times(N_TRIALS).run();

        System.out.println("\nSizes:");
        measureBallSize(type, CodeSynthesisTest.COST_LIMIT);
        Thread.sleep(10);
        Assert.assertTrue("Synthesis did not return the desired segment", passed[0]);
    }

    @Test
    public void testBufferedReader() throws Exception {
        testSynthesis("java.io.BufferedReader", "new BufferedReader(new FileReader(fileName))");
    }

    @Test
    public void testSequenceInputStream() throws Exception {
        testSynthesis("java.io.SequenceInputStream", "new SequenceInputStream(inStream1, inStream2)");
    }

    @Test
    public void testFileInputStream() throws Exception {
        testSynthesis("java.io.FileInputStream", "new FileInputStream(fileName)");
    }

    @Test
    public void testInputStreamReader() throws Exception {
        testSynthesis("java.io.InputStreamReader", "new InputStreamReader(inStream1)");
    }

    @Test
    public void testMatcher() throws Exception {
        testSynthesis("java.util.regex.Matcher", "(Pattern.compile(fileName)).matcher(inputText)");
    }

    @Test
    public void testAudioClip() throws Exception {
        testSynthesis("java.applet.AudioClip", "Applet.newAudioClip(new URL(fileName))");
    }

    @Test
    public void testInputStreamFromByte() throws Exception {
        testGraph.resetLocals();
        testGraph.addLocalVariable("b", "byte[]");
        testSynthesis("java.io.InputStream", "new ByteArrayInputStream(b)");
    }
}
