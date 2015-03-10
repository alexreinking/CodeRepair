package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;

public class FieldSynthesizer implements Serializable, JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals) {
        if (formals.length < 1)
            throw new IllegalArgumentException("Methods must at least have an owner");
        return String.format("(%s).%s", formals[0].code, functionName);
    }
}
