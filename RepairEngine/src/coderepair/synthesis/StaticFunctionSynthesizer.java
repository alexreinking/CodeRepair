package coderepair.synthesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StaticFunctionSynthesizer extends JavaFunctionSynthesizer implements Serializable {
    @Override
    public String synthesizeFromArguments(String functionName, List<CodeSnippet> formals) {
        return String.format("%s(%s)", functionName, String.join(", ", getStrings(formals)));
    }

    private List<String> getStrings(List<CodeSnippet> snippets)
    {
        List<String> args = new ArrayList<String>(snippets.size());
        for (CodeSnippet snippet : snippets) args.add(snippet.code);
        return args;
    }
}
