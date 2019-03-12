package coderepair.synthesis;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.trees.ExpressionTree;
import coderepair.synthesis.valuations.RepairValuator;
import coderepair.util.GraphLoader;
import coderepair.util.TimedTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.SortedSet;

public class CodeSynthesisTest {
    private static final double CUT_TARGET = 0.8;
    private static final int REQUESTED = 10;
    private static final int N_TRIALS = 1;
    private static final String inFile = "../resources/rt.javap";
    private static final String graphFile = "../resources/graph.ser";
    private static SynthesisGraph testGraph;

    @Before
    public void setUp() {
        if (testGraph == null) {
            testGraph = GraphLoader.fromSerialized(graphFile, inFile);
            if (testGraph == null) throw new RuntimeException("Could not load graph");
        }
    }

    @Test
    public void testDatagramPacket() throws Exception {
        testSynthesis(
                "java.net.DatagramPacket",
                "new DatagramPacket(buf, buf.length)",
                new EnvironmentEntry("byte[]", "buf"),
                new EnvironmentEntry("int", "buf.length")
        );
    }

    private void testSynthesis(String type, String desiredCode, EnvironmentEntry... environment) throws InterruptedException {
        final boolean[] passed = {false};

        RepairValuator valuator = new RepairValuator(testGraph);
        CodeSynthesis synthesis = new CodeSynthesis(testGraph, new ExpressionTreeBuilder(valuator));

        for (EnvironmentEntry var : environment)
            valuator.strongEnforce(var.qualifiedType, var.statement);


        new TimedTask("Synthesis", () -> {
            System.out.println("\n============= " + type + " =============\n");
            SortedSet<ExpressionTree> expressions = synthesis.synthesize(type,
                    CodeSynthesisTest.CUT_TARGET, CodeSynthesisTest.REQUESTED);

            for (ExpressionTree snippet : expressions) {
                if (snippet.asExpression().equals(desiredCode)) {
                    System.out.print("* ");
                    passed[0] = true;
                }
                System.out.printf("%6f  %s%n", snippet.getCost(), snippet.asExpression());
            }
        }).times(N_TRIALS).run();

        valuator.relax();

        Thread.sleep(10);
        Assert.assertTrue("Synthesis did not return the desired segment", passed[0]);
    }

    @Test
    public void testInetAddress() throws Exception {
        testSynthesis(
                "java.net.InetAddress",
                "InetAddress.getByName(mcIPStr)",
                new EnvironmentEntry("java.lang.String", "mcIPStr")
        );
    }

    @Test
    public void testURLConnection() throws Exception {
        testSynthesis(
                "java.net.URLConnection",
                "(new URL(urlStr)).openConnection()",
                new EnvironmentEntry("java.lang.String", "urlStr")
        );
    }

    // From InSynth
    @Test
    public void testTransferHandler() throws Exception {
        testSynthesis(
                "javax.swing.TransferHandler",
                "new TransferHandler(s)",
                new EnvironmentEntry("java.lang.String", "s")
        );
    }

    // From InSynth
    @Test
    public void testSwingTimer() throws Exception {
        testSynthesis(
                "javax.swing.Timer",
                "new Timer(500, actionListener)",
                new EnvironmentEntry("int", "500"),
                new EnvironmentEntry("java.awt.event.ActionListener", "actionListener")
        );
    }

    @Test
    public void testGraphicsDevice() throws Exception {
        testSynthesis(
                "java.awt.GraphicsDevice",
                "(GraphicsEnvironment.getLocalGraphicsEnvironment()).getDefaultScreenDevice()"
        );
    }

    @Test
    public void testImageIcon() throws Exception {
        testSynthesis(
                "javax.swing.ImageIcon",
                "new ImageIcon(\"yourFile.gif\")",
                new EnvironmentEntry("java.lang.String", "\"yourFile.gif\"")
        );
    }

    @Test
    public void testGradientPaint() throws Exception {
        testSynthesis(
                "java.awt.GradientPaint",
                "new GradientPaint(endPointX, endPointY, endColor, startPointX, startPointY, startColor)",
                new EnvironmentEntry("java.awt.Color", "endColor"),
                new EnvironmentEntry("float", "endPointY"),
                new EnvironmentEntry("float", "endPointX"),
                new EnvironmentEntry("java.awt.Color", "startColor"),
                new EnvironmentEntry("float", "startPointY"),
                new EnvironmentEntry("float", "startPointX")
        );
    }

    @Test
    public void testJFrame() throws Exception {
        testSynthesis(
                "javax.swing.JFrame",
                "new JFrame(\"Label!\")",
                new EnvironmentEntry("java.lang.String", "\"Label!\"")
        );
    }

    @Test
    public void testJTextArea() throws Exception {
        testSynthesis(
                "javax.swing.JTextArea",
                "new JTextArea(\"Label!\")",
                new EnvironmentEntry("java.lang.String", "\"Label!\"")
        );
    }

    @Test
    public void testJCheckBox() throws Exception {
        testSynthesis(
                "javax.swing.JCheckBox",
                "new JCheckBox(\"Label!\")",
                new EnvironmentEntry("java.lang.String", "\"Label!\"")
        );
    }

    @Test
    public void testJButton() throws Exception {
        testSynthesis(
                "javax.swing.JButton",
                "new JButton(\"Button 1!\")",
                new EnvironmentEntry("java.lang.String", "\"Button 1!\"")
        );
    }

    @Test
    public void testMaskFormatter() throws Exception {
        testSynthesis(
                "javax.swing.text.MaskFormatter",
                "new MaskFormatter(\"Label!\")",
                new EnvironmentEntry("java.lang.String", "\"Label!\"")
        );
    }

    @Test
    public void testContainerFromFrame() throws Exception {
        testSynthesis(
                "java.awt.Container",
                "(frame).getContentPane()",
                new EnvironmentEntry("javax.swing.JFrame", "frame")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_io/0010__Java_io_File.htm
    @Test
    public void testExists() throws Exception {
        testSynthesis(
                "boolean",
                "(dummyFile).exists()",
                new EnvironmentEntry("java.io.File", "dummyFile")
        );
    }

    @Test
    public void testBufferedReader() throws Exception {
        testSynthesis(
                "java.io.BufferedReader",
                "new BufferedReader(new FileReader(fileName))",
                new EnvironmentEntry("java.lang.String", "fileName")
        );
    }

    @Test
    public void testStringReader() throws Exception {
        testSynthesis(
                "java.io.StringReader",
                "new StringReader(\"a bc ddd\")",
                new EnvironmentEntry("java.lang.String", "\"a bc ddd\"")
        );
    }

    @Test
    public void testBufferedWriter() throws Exception {
        testSynthesis(
                "java.io.BufferedWriter",
                "new BufferedWriter(new PrintWriter(fileName))",
                new EnvironmentEntry("java.lang.String", "fileName")
        );
    }

    @Test
    public void testPrintStream() throws Exception {
        testSynthesis(
                "java.io.PrintStream",
                "new PrintStream(fout)",
                new EnvironmentEntry("java.io.FileOutputStream", "fout"),
                new EnvironmentEntry("java.io.File", "file")
        );
    }

    @Test
    public void testPipedReader() throws Exception {
        testSynthesis(
                "java.io.PipedReader",
                "new PipedReader()"
        );
    }

    @Test
    public void testPipedReaderFromWriter() throws Exception {
        testSynthesis(
                "java.io.PipedReader",
                "new PipedReader(pw)",
                new EnvironmentEntry("java.io.PipedWriter", "pw")
        );
    }

    // From InSynth
    @Test
    public void testObjectOutputStream() throws Exception {
        testSynthesis(
                "java.io.ObjectOutputStream",
                "new ObjectOutputStream(fos)",
                new EnvironmentEntry("java.io.FileOutputStream", "fos")
        );
    }

    // From InSynth
    @Test
    public void testObjectInputStream() throws Exception {
        testSynthesis(
                "java.io.ObjectInputStream",
                "new ObjectInputStream(fis)",
                new EnvironmentEntry("java.io.FileInputStream", "fis"),
                new EnvironmentEntry("java.io.FileOutputStream", "fos"),
                new EnvironmentEntry("java.io.ObjectOutputStream", "oos")
        );
    }

    @Test
    public void testLineNumberReader() throws Exception {
        testSynthesis(
                "java.io.LineNumberReader",
                "new LineNumberReader((System.console()).reader())"
        );
    }

    @Test
    public void testJWindow() throws Exception {
        testSynthesis(
                "javax.swing.JWindow",
                "new JWindow()"
        );
    }

    @Test
    public void testJViewport() throws Exception {
        testSynthesis(
                "javax.swing.JViewport",
                "new JViewport()"
        );
    }

    @Test
    public void testJTree() throws Exception {
        testSynthesis(
                "javax.swing.JTree",
                "new JTree(top)",
                new EnvironmentEntry("javax.swing.tree.DefaultMutableTreeNode", "top")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_io/0110__Java_io_BufferedInputStream.htm
    @Test
    public void testBufferedInputStream() throws Exception {
        testSynthesis(
                "java.io.BufferedInputStream",
                "new BufferedInputStream(new FileInputStream(srcFile))",
                new EnvironmentEntry("java.lang.String", "srcFile")
        );
    }

    @Test
    public void testSequenceInputStream() throws Exception {
        testSynthesis(
                "java.io.SequenceInputStream",
                "new SequenceInputStream(inStream1, inStream2)",
                new EnvironmentEntry("java.io.InputStream", "inStream1"),
                new EnvironmentEntry("java.io.InputStream", "inStream2")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_io/0730__Java_io_Scanner.htm
    @Test
    public void testScanner() throws Exception {
        testSynthesis(
                "java.util.Scanner",
                "new Scanner(System.in)"
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_XML/0100__Java_DOM_Intro.htm
    @Test
    public void testDocumentBuilder() throws Exception {
        testSynthesis(
                "javax.xml.parsers.DocumentBuilder",
                "(DocumentBuilderFactory.newInstance()).newDocumentBuilder()"
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_XML/0100__Java_DOM_Intro.htm
    @Test
    public void testDocument() throws Exception {
        testSynthesis(
                "org.w3c.dom.Document",
                "((factory).newDocumentBuilder()).parse(\"file:///Users/alex/file.xml\")",
                new EnvironmentEntry("java.lang.String", "\"file:///Users/alex/file.xml\""),
                new EnvironmentEntry("javax.xml.parsers.DocumentBuilderFactory", "factory")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_XML/0100__Java_DOM_Intro.htm
    @Test
    public void testNodeList() throws Exception {
        testSynthesis(
                "org.w3c.dom.NodeList",
                "(node).getChildNodes()",
                new EnvironmentEntry("org.w3c.dom.Node", "node")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_io/0730__Java_io_Scanner.htm
    @Test
    public void testDoubleFromScanner() throws Exception {
        testSynthesis(
                "double",
                "(scanner).nextDouble()",
                new EnvironmentEntry("java.util.Scanner", "scanner")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_Network/0010__Java_Network_TCP_Server.htm
    @Test
    public void testInetSocketAddress() throws Exception {
        testSynthesis(
                "java.net.InetSocketAddress",
                "new InetSocketAddress(\"localhost\", 12900)",
                new EnvironmentEntry("java.lang.String", "\"localhost\""),
                new EnvironmentEntry("int", "12900")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_Network/0010__Java_Network_TCP_Server.htm
    @Test
    public void testServerSocket() throws Exception {
        testSynthesis(
                "java.net.ServerSocket",
                "new ServerSocket(12900, 100)",
                new EnvironmentEntry("int", "12900"),
                new EnvironmentEntry("int", "100")
        );
    }

    @Test
    public void testInputStreamSocket() throws Exception {
        testSynthesis(
                "java.io.InputStream",
                "(activeSocket).getInputStream()",
                new EnvironmentEntry("java.net.Socket", "activeSocket")
        );
    }

    @Test
    public void testStringTokenizer() throws Exception {
        testSynthesis(
                "java.util.StringTokenizer",
                "new StringTokenizer(str, delimiters)",
                new EnvironmentEntry("java.lang.String", "str"),
                new EnvironmentEntry("java.lang.String", "delimiters")
        );
    }

    // From InSynth
    @Test
    public void testStreamTokenizer() throws Exception {
        testSynthesis(
                "java.io.StreamTokenizer",
                "new StreamTokenizer(br)",
                new EnvironmentEntry("java.io.BufferedReader", "br"),
                new EnvironmentEntry("java.io.FileReader", "fr")
        );
    }

    @Test
    public void testGetToken() throws Exception {
        testSynthesis(
                "java.lang.String",
                "(st).nextToken()",
                new EnvironmentEntry("java.util.StringTokenizer", "st")
        );
    }

    /*
     * Need type inference to make this one work.
     */
//    @Test
//    public void testBoxLayout() throws Exception {
//        testSynthesis(
//                "javax.swing.BoxLayout",
//                "new BoxLayout(menuBar, BoxLayout.PAGE_AXIS)",
//                new EnvironmentEntry("javax.swing.JMenuBar", "menuBar")
//        );
//    }

    @Test
    public void testBorder() throws Exception {
        testSynthesis(
                "javax.swing.border.Border",
                "BorderFactory.createEtchedBorder(0, Color.BLACK, Color.BLACK)",
                new EnvironmentEntry("int", "0"),
                new EnvironmentEntry("java.awt.Color", "Color.BLACK")
        );
    }

    // http://www.java2s.com/Tutorials/Java/Java_io/0100__Java_io_FileInputStream.htm
    @Test
    public void testFileInputStream() throws Exception {
        testSynthesis(
                "java.io.FileInputStream",
                "new FileInputStream(fileName)",
                new EnvironmentEntry("java.lang.String", "fileName")
        );
    }

    /*
    // http://www.java2s.com/Tutorials/Java/Java_io/0100__Java_io_FileInputStream.htm
    @Test
    public void testByteFromFileInputStream() throws Exception {
        testSynthesis(
                "byte",
                "(fin).read()",
                new EnvironmentEntry("java.io.FileInputStream", "fin")
        );
    }
    */

    @Test
    public void testInputStreamReaderFromNothing() throws Exception {
        testSynthesis(
                "java.io.InputStreamReader",
                "new InputStreamReader(System.in)"
        );
    }

    @Test
    public void testInputStreamReader() throws Exception {
        testSynthesis(
                "java.io.InputStreamReader",
                "new InputStreamReader(inStream1)",
                new EnvironmentEntry("java.io.InputStream", "inStream1")
        );
    }

    @Test
    public void testMatcher() throws Exception {
        testSynthesis(
                "java.util.regex.Matcher",
                "(Pattern.compile(fileName)).matcher(inputText)",
                new EnvironmentEntry("java.lang.String", "fileName"),
                new EnvironmentEntry("java.lang.String", "inputText")
        );
    }

    @Test
    public void testPattern() throws Exception {
        testSynthesis(
                "java.util.regex.Pattern",
                "Pattern.compile(fileName)",
                new EnvironmentEntry("java.lang.String", "fileName")
        );
    }

    @Test
    public void testString() throws Exception {
        testSynthesis(
                "java.lang.String",
                "fileName",
                new EnvironmentEntry("java.lang.String", "fileName")
        );
    }

    @Test
    public void testAudioClip() throws Exception {
        testSynthesis(
                "java.applet.AudioClip",
                "Applet.newAudioClip(new URL(fileName))",
                new EnvironmentEntry("java.lang.String", "fileName")
        );
    }

    @Test
    public void testURL() throws Exception {
        testSynthesis(
                "java.net.URL",
                "new URL(fileName)",
                new EnvironmentEntry("java.lang.String", "fileName")
        );
    }

    @Test
    public void testInputStreamFromByte() throws Exception {
        testSynthesis(
                "java.io.InputStream",
                "new ByteArrayInputStream(buffer)",
                new EnvironmentEntry("byte[]", "buffer")
        );
    }

    private class EnvironmentEntry {
        public final String qualifiedType;
        public final String statement;

        public EnvironmentEntry(String qualifiedType, String statement) {
            this.qualifiedType = qualifiedType;
            this.statement = statement;
        }
    }
}
