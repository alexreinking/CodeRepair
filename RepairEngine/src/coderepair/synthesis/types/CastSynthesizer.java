package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alex on 10/21/14.
 */
public class CastSynthesizer extends JavaFunctionSynthesizer implements Serializable {
    @Override
    public String synthesizeFromArguments(String functionName, List<CodeSnippet> formals) {
        if (formals.size() != 1)
            throw new IllegalArgumentException("Error! Can only cast exactly one type to another");
        return formals.get(0).code;
    }
}
