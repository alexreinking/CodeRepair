package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;
import java.util.List;

public class FieldSynthesizer extends JavaFunctionSynthesizer implements Serializable {
    @Override
    public String synthesizeFromArguments(String functionName, List<CodeSnippet> formals) {
        if (formals.size() < 1)
            throw new IllegalArgumentException("Methods must at least have an owner");
        return String.format("(%s).%s", formals.get(0).code, functionName);
    }
}
