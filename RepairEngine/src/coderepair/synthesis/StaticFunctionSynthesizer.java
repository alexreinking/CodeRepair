package coderepair.synthesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaticFunctionSynthesizer extends JavaFunctionSynthesizer implements Serializable {
    @Override
    public String synthesizeFromArguments(String functionName, List<CodeSnippet> formals) {
        return String.format("%s(%s)", functionName, String.join(", ", getStrings(formals)));
    }

    private List<String> getStrings(List<CodeSnippet> snippets) {
        return new ArrayList<>(snippets.stream().map(snippet -> snippet.code).collect(Collectors.toList()));
    }
}
