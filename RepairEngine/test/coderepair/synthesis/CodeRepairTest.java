package coderepair.synthesis;

import coderepair.SynthesisGraph;
import coderepair.util.GraphLoader;
import com.intellij.util.Producer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ajr64 on 11/13/14.
 */
public class CodeRepairTest {
    final private static String inFile = "./resources/rt.javap";
    final private static String graphFile = "./resources/graph.ser";
    private SynthesisGraph synthesisGraph;
    private static final Producer<Double> costs = () -> 0.001 * Math.random();
    private CodeSnippet bestSnippet;

    @Before
    public void setUp() throws Exception {
        synthesisGraph = GraphLoader.getGraph(graphFile, inFile);
        if (synthesisGraph == null) throw new RuntimeException("Could not load graph");

        synthesisGraph.addLocalVariable("fileName", "java.lang.String", costs.produce());
        synthesisGraph.addLocalVariable("inputText", "java.lang.String", costs.produce());
        synthesisGraph.addLocalVariable("inStream", "java.io.InputStream", costs.produce());
        synthesisGraph.addLocalVariable("outStream", "java.io.OutputStream", costs.produce());
    }

    @Test
    public void testRepairDeflater() {
        synthesisGraph.resetLocals();
        synthesisGraph.addLocalVariable("fileName", "java.lang.String");

        CodeSynthesis synthesis = new CodeSynthesis(synthesisGraph);

        /* Stage 1 */
        bestSnippet = synthesis.synthesize("java.io.FileInputStream", 6.5, 1).first();

        System.out.printf("%6f  %s%n", bestSnippet.cost, bestSnippet.code);
        synthesis.strongEnforce("java.io.FileInputStream", new CodeSnippet(bestSnippet.code, costs.produce()));

        /* Stage 2 */
        synthesis.strongEnforce("boolean", new CodeSnippet("true", costs.produce()));
        synthesis.strongEnforce("int", new CodeSnippet("compLevel", costs.produce()));

        bestSnippet = synthesis.synthesize("java.util.zip.DeflaterInputStream", 6.5, 1).first();

        System.out.printf("%6f  %s%n", bestSnippet.cost, bestSnippet.code);
        synthesis.strongEnforce("java.util.zip.DeflaterInputStream", new CodeSnippet(bestSnippet.code, costs.produce()));

        /* Stage 3 */
        synthesis.strongEnforce("int", new CodeSnippet("buffSize", costs.produce()));

        bestSnippet = synthesis.synthesize("java.io.BufferedInputStream", 6.5, 1).first();

        System.out.printf("%6f  %s%n", bestSnippet.cost, bestSnippet.code);
        synthesis.strongEnforce("java.io.BufferedInputStream", new CodeSnippet(bestSnippet.code, costs.produce()));

        /* Confirm Result */
        assertEquals("Repair failed",
                "new BufferedInputStream(new DeflaterInputStream(new FileInputStream(fileName), new Deflater(compLevel, true)), buffSize)",
                bestSnippet.code
        );
    }

    @Test
    public void repairBufferedReader() {
        CodeSynthesis synthesis = new CodeSynthesis(synthesisGraph);

        /* Stage 1 */
        synthesis.strongEnforce("java.lang.String", new CodeSnippet("fileName", costs.produce()));

        bestSnippet = synthesis.synthesize("java.io.BufferedReader", 6.5, 10).first();

        System.out.printf("%6f  %s%n", bestSnippet.cost, bestSnippet.code);
        synthesis.strongEnforce("java.io.BufferedReader", new CodeSnippet(bestSnippet.code, costs.produce()));

        /* Confirm Result */
        assertEquals("Repair failed",
                "new BufferedReader(new StringReader(fileName))",
                bestSnippet.code
        );
    }

    @Test
    public void repairSequenceInputStream() {
        CodeSynthesis synthesis = new CodeSynthesis(synthesisGraph);

        /* Stage 1 */
        synthesis.strongEnforce("java.lang.String", new CodeSnippet("body", costs.produce()));
        synthesis.strongEnforce("java.lang.String", new CodeSnippet("sig", costs.produce()));

        bestSnippet = synthesis.synthesize("java.io.SequenceInputStream", 6.5, 10).first();

        synthesis.strongEnforce("java.io.SequenceInputStream", new CodeSnippet(bestSnippet.code, costs.produce()));

        /* Confirm Result */
        assertEquals("Repair failed",
                "new SequenceInputStream(new FileInputStream(body), new FileInputStream(sig))",
                bestSnippet.code
        );
    }
}
