package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

@FunctionalInterface
public interface JavaFunctionSynthesizer {
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals);
}
