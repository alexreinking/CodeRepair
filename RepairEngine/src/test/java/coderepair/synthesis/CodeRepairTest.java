package coderepair.synthesis;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.ExpressionTree;
import coderepair.synthesis.valuations.RepairValuator;
import coderepair.util.GraphLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.SortedSet;

import static org.junit.Assert.assertEquals;

public class CodeRepairTest {
    private final static String inFile = "./resources/rt.javap";
    private final static String graphFile = "./resources/graph.ser";
    private final static double CUT_TARGET = 0.6;
    private static SynthesisGraph synthesisGraph = null;
    private static RepairValuator repairValuator;
    private static CodeSynthesis synthesis;

    @Before
    public void setUp() {
        if (synthesisGraph == null) {
            synthesisGraph = GraphLoader.fromSerialized(graphFile, inFile);
            if (synthesisGraph == null) throw new RuntimeException("Could not load graph");
            repairValuator = new RepairValuator(synthesisGraph);
        }

        synthesisGraph.resetLocals();
        repairValuator.relax();
        synthesisGraph.addLocalVariable("fileName", "java.lang.String");
        synthesisGraph.addLocalVariable("inputText", "java.lang.String");
        synthesisGraph.addLocalVariable("inStream", "java.io.InputStream");
        synthesisGraph.addLocalVariable("outStream", "java.io.OutputStream");
        synthesis = new CodeSynthesis(synthesisGraph, new ExpressionTreeBuilder(repairValuator));
    }

    @After
    public void done() throws InterruptedException {
        System.out.println("Done!");
        System.out.println();
        Thread.sleep(10);
    }

    /**
     * The original line of code in this example was:
     * new BufferedInputStream(buffSize, new DeflaterInputStream(new FileInputStream, compLevel, true))
     */
    @Test
    public void testRepairDeflater() {
        synthesisGraph.resetLocals();
        synthesisGraph.addLocalVariable("fileName", "java.lang.String");

        /* Stage 1 */
        ExpressionTree bestSnippet = synthesis.synthesize("java.io.FileInputStream", CUT_TARGET, 5).first();

        System.out.printf("%6f  %s%n", bestSnippet.getCost(), bestSnippet.asExpression());
        repairValuator.relax();
        repairValuator.strongEnforce(bestSnippet);

        /* Stage 2 */
        repairValuator.strongEnforce("boolean", "true");
        repairValuator.strongEnforce("int", "compLevel");
        bestSnippet = synthesis.synthesize("java.util.zip.DeflaterInputStream", CUT_TARGET, 5).first();

        System.out.printf("%6f  %s%n", bestSnippet.getCost(), bestSnippet.asExpression());
        repairValuator.relax();
        repairValuator.strongEnforce(bestSnippet);

        /* Stage 3 */
        repairValuator.strongEnforce("int", "buffSize");

        bestSnippet = synthesis.synthesize("java.io.BufferedInputStream", CUT_TARGET, 5).first();

        System.out.printf("%6f  %s%n", bestSnippet.getCost(), bestSnippet.asExpression());
        repairValuator.relax();
        repairValuator.strongEnforce(bestSnippet);

        /* Confirm Result */
        assertEquals("Repair failed",
                "new BufferedInputStream(new DeflaterInputStream(new FileInputStream(fileName), new Deflater(compLevel, true)), buffSize)",
                bestSnippet.asExpression()
        );
    }

    /**
     * The original line of code in this example was:
     * new BufferedReader(fileName)
     */

    @Test
    public void repairBufferedReader() {
        /* Stage 1 */
        repairValuator.strongEnforce("java.lang.String", "fileName");

        ExpressionTree bestSnippet = synthesis.synthesize("java.io.BufferedReader", CUT_TARGET, 1).first();

        System.out.printf("%6f  %s%n", bestSnippet.getCost(), bestSnippet.asExpression());
        repairValuator.strongEnforce(bestSnippet);

        /* Confirm Result */
        assertEquals("Repair failed",
                "new BufferedReader(new FileReader(fileName))",
                bestSnippet.asExpression()
        );
    }

    /**
     * The original line of code in this example was:
     * new SequenceInputStream(body, sig)
     */
    @Test
    public void repairSequenceInputStream() {
        /* Stage 1 */
        repairValuator.strongEnforce("java.lang.String", "body");
        repairValuator.strongEnforce("java.lang.String", "sig");

        SortedSet<ExpressionTree> results = synthesis.synthesize("java.io.SequenceInputStream", CUT_TARGET, 10);
        ExpressionTree bestSnippet = results.first();

        for (ExpressionTree result : results) {
            System.out.printf("%6f  %s%n", result.getCost(), result.asExpression());
        }

        System.out.printf("%n* %6f  %s%n", bestSnippet.getCost(), bestSnippet.asExpression());
        repairValuator.strongEnforce(bestSnippet);

        /* Confirm Result */
        assertEquals("Repair failed",
                "new SequenceInputStream(new FileInputStream((body)), new FileInputStream((sig)))",
                bestSnippet.asExpression()
        );
    }
}
