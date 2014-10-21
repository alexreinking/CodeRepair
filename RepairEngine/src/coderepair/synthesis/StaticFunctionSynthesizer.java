package coderepair.synthesis;

import java.util.ArrayList;
import java.util.List;

public class StaticFunctionSynthesizer extends JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, List<Snippet> formals) {
        return String.format("%s(%s)", functionName, String.join(", ", getStrings(formals)));
    }

    private List<String> getStrings(List<Snippet> snippets)
    {
        List<String> args = new ArrayList<String>(snippets.size());
        for (Snippet snippet : snippets) args.add(snippet.code);
        return args;
    }
}
