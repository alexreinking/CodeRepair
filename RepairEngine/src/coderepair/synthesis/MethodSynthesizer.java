package coderepair.synthesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodSynthesizer extends JavaFunctionSynthesizer implements Serializable {
    @Override
    public String synthesizeFromArguments(String functionName, List<CodeSnippet> formals) {
        if (formals.size() < 1)
            throw new IllegalArgumentException("Methods must at least have an owner");
        List<String> strings = getStrings(formals.subList(1, formals.size()));
        return String.format("(%s).%s(%s)", formals.get(0).code, functionName, String.join(", ", strings));
    }

    private List<String> getStrings(List<CodeSnippet> snippets)
    {
        return new ArrayList<>(snippets.stream().map(snippet -> snippet.code).collect(Collectors.toList()));
    }
}
