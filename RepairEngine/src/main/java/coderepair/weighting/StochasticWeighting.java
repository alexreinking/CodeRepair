package coderepair.weighting;

import coderepair.graph.JavaFunctionNode;
import coderepair.graph.JavaGraphNode;
import coderepair.graph.JavaTypeNode;
import coderepair.graph.SynthesisGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexreinking on 3/9/15.
 */
public class StochasticWeighting implements GraphWeighter {
    private static final String apiUrl = "https://searchcode.com/api/codesearch_I/?per_page=1&lan=23&q=";
    private final UnaryOperator<Double> freqToScore;

    public StochasticWeighting(UnaryOperator<Double> freqToScore) {
        this.freqToScore = freqToScore;
    }

    @Override
    public void applyWeight(SynthesisGraph graph) {
        Set<URL> queryValues = Collections.synchronizedSet(new HashSet<>(graph.vertexSet().size()));

        graph.edgeSet().forEach(edge -> graph.setEdgeWeight(edge, 0.0));
        graph.vertexSet().parallelStream().filter(j -> j instanceof JavaFunctionNode).forEach(n -> {
            JavaFunctionNode fn = (JavaFunctionNode) n;
            String functionName = fn.getFunctionName();

            if (!fn.getKind().equals(JavaGraphNode.Kind.ClassCast)) {
                String query = null;

                if (fn.getKind().equals(JavaGraphNode.Kind.Constructor)) {
                    query = functionName;
                } else if (fn.getKind().equals(JavaGraphNode.Kind.StaticMethod)) {
                    query = functionName;
                } else {
                    JavaTypeNode javaTypeNode = fn.getOutput();
                    query = javaTypeNode.getPackageName() + " " + javaTypeNode.getClassName() + " " + functionName;
                }

                if (query.contains("<") || query.contains(">"))
                    return; // ignore generics for now

                try {
                    URL apiCall = new URL(apiUrl + URLEncoder.encode(query, "UTF-8"));

                    if (!queryValues.contains(apiCall)) {
                        HttpURLConnection apiConn = (HttpURLConnection) apiCall.openConnection();
                        apiConn.setRequestMethod("GET");
                        apiConn.setRequestProperty("User-Agent", "Mozilla/5.0");

                        if (apiConn.getResponseCode() == 200) {
                            BufferedReader resp = new BufferedReader(new InputStreamReader(apiConn.getInputStream()));
                            String line;
                            while ((line = resp.readLine()) != null) {
                                Matcher totalMatcher = Pattern.compile("\"total\": (\\d+)").matcher(line);
                                if (totalMatcher.find()) {
                                    int freq = Integer.parseInt(totalMatcher.group(1));
                                    double weight = (freq < 10)
                                            ? Double.POSITIVE_INFINITY
                                            : freqToScore.apply((double) freq);

                                    graph.setEdgeWeight(graph.getEdge(fn.getOutput(), fn), weight);
                                    System.out.printf("result (%7d -> %4f) : %s%n", freq, weight, fn.getName());
                                } else {
                                    System.err.printf("error: no results for %s%n", apiCall.toString());
                                    graph.setEdgeWeight(graph.getEdge(fn.getOutput(), fn), Double.MAX_VALUE);
                                }
                            }
                            resp.close();
                        }

                        queryValues.add(apiCall);
                    }
                } catch (UnsupportedEncodingException e) {
                    System.err.println("fatal: your system does not support UTF-8");
                    System.exit(1);
                } catch (MalformedURLException e) {
                    System.err.println("fatal: somehow, a java symbol broke the W3C URL spec.");
                    System.err.println(e.getMessage());
                    System.exit(1);
                } catch (IOException e) {
                    System.err.println("fatal: failed to open connection to SearchCode");
                    System.exit(1);
                }
            }
        });


    }
}
