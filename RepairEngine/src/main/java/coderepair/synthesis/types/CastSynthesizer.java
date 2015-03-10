package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;

/**
 * Created by alex on 10/21/14.
 */
public class CastSynthesizer implements Serializable, JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals) {
        if (formals.length != 1)
            throw new IllegalArgumentException("Error! Can only cast exactly one type to another");
        return formals[0].code;
    }
}
