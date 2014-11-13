package coderepair.synthesis;

import coderepair.SynthesisGraph;
import coderepair.util.GraphLoader;
import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CodeSynthesisTest extends AbstractBenchmark {
    private CodeSynthesis synthesis = null;
    final private static String inFile = "./resources/rt.javap";
    final private static String graphFile = "./resources/graph.ser";

    @Before
    public void setUp() throws Exception {
        SynthesisGraph testGraph = GraphLoader.getGraph(graphFile, inFile);
        if (testGraph == null) throw new RuntimeException("Could not load graph");

        synthesis = new CodeSynthesis(testGraph);

        testGraph.addLocalVariable("fileName", "java.lang.String");
        testGraph.addLocalVariable("inputText", "java.lang.String");
        testGraph.addLocalVariable("inStream", "java.io.InputStream");
        testGraph.addLocalVariable("outStream", "java.io.OutputStream");
    }

    private void testSynthesis(String type, String desiredCode, double costLimit, int nRequested) {
        System.out.println("\n============= " + type + " =============\n");

        boolean passed = false;
        for (CodeSnippet snippet : synthesis.synthesize(type, costLimit, nRequested)) {
            if (snippet.code.equals(desiredCode)) {
                System.out.print("* ");
                passed = true;
            }
            System.out.printf("%6f  %s%n", snippet.cost, snippet.code);
        }

        System.out.flush();
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