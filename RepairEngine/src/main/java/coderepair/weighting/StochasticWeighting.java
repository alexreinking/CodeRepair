package coderepair.weighting;

import coderepair.graph.JavaFunctionNode;
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
import java.util.HashMap;
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

    public void applyWeight(SynthesisGraph graph) {
        HashMap<URL, Double> queryValues = new HashMap<>(graph.vertexSet().size());
        final int[] nRead = {0};

        graph.vertexSet().stream().filter(j -> j instanceof JavaFunctionNode).forEach(n -> {
            JavaFunctionNode fn = (JavaFunctionNode) n;
            String functionName = fn.getFunctionName();
            JavaTypeNode output = fn.getOutput();

            if (!functionName.equals("<cast>")) {
                String query = null;

                if (functionName.startsWith("new ")) {
                    query = functionName;
                } else if (functionName.contains(".")) {
                    query = functionName;
                } else {
                    JavaTypeNode javaTypeNode = fn.getSignature().get(0);
                    query = javaTypeNode.getPackageName() + " " + javaTypeNode.getClassName() + " " + functionName;
                }

                if (query.contains("<") || query.contains(">"))
                    return; // ignore generics for now

                try {
                    URL apiCall = new URL(apiUrl + URLEncoder.encode(query, "UTF-8"));

                    if (!queryValues.containsKey(apiCall)) {
                        HttpURLConnection apiConn = (HttpURLConnection) apiCall.openConnection();
                        apiConn.setRequestMethod("GET");
                        apiConn.setRequestProperty("User-Agent", "Mozilla/5.0");

                        int total = 0;
                        if (apiConn.getResponseCode() == 200) {
                            BufferedReader resp = new BufferedReader(new InputStreamReader(apiConn.getInputStream()));
                            String line;
                            while ((line = resp.readLine()) != null) {
                                Matcher totalMatcher = Pattern.compile("\"total\": (\\d+)").matcher(line);
                                if (totalMatcher.find()) {
                                    total = Integer.parseInt(totalMatcher.group(1));
                                    nRead[0]++;
                                    System.out.printf("result (%6d) = (%9d) : %s -> %s%n", nRead[0], total, query, output.getPackageName() + "." + output.getClassName());
                                } else {
                                    System.err.printf("error: no results for %s%n", apiCall.toString());
                                }
                            }
                            resp.close();
                        }

                        queryValues.put(apiCall, freqToScore.apply((double) total));
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
