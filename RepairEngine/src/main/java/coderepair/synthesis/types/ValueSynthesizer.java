package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;
import java.util.List;

public class ValueSynthesizer extends JavaFunctionSynthesizer implements Serializable {
    @Override
    public String synthesizeFromArguments(String functionName, List<CodeSnippet> formals) {
        if (formals.size() != 0)
            throw new IllegalArgumentException("Values may not take arguments");
        return functionName;
    }
}
