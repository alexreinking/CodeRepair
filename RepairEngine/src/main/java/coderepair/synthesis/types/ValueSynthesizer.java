package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;

public class ValueSynthesizer implements Serializable, JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals) {
        if (formals.length != 0)
            throw new IllegalArgumentException("Values may not take arguments");
        return functionName;
    }
}
