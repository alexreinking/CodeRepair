package coderepair.synthesis;

import java.util.ArrayList;
import java.util.List;

public class MethodSynthesizer extends JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, List<CodeSnippet> formals) {
        if (formals.size() < 1)
            throw new IllegalArgumentException("Methods must at least have an owner");
        List<String> strings = getStrings(formals.subList(1, formals.size()));
        return String.format("(%s).%s(%s)", formals.get(0).code, functionName, String.join(", ", strings));
    }

    private List<String> getStrings(List<CodeSnippet> snippets)
    {
        List<String> args = new ArrayList<String>(snippets.size());
        for (CodeSnippet snippet : snippets) args.add(snippet.code);
        return args;
    }
}
