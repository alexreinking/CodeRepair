package coderepair.synthesis;

import java.util.List;

public class ValueSynthesizer extends JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, List<Snippet> formals) {
        if (formals.size() != 0)
            throw new IllegalArgumentException("Values may not take arguments");
        return functionName;
    }
}
