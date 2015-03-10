package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;
import java.util.ArrayList;

public class StaticFunctionSynthesizer implements Serializable, JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals) {
        ArrayList<String> args = new ArrayList<>(formals.length);
        for (CodeSnippet formal : formals) args.add(formal.code);
        return String.format("%s(%s)", functionName, String.join(", ", args));
    }

}
