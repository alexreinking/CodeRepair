package coderepair.synthesis.types;

import coderepair.synthesis.CodeSnippet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MethodSynthesizer implements Serializable, JavaFunctionSynthesizer {
    @Override
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals) {
        if (formals.length < 1)
            throw new IllegalArgumentException("Methods must at least have an owner");
        List<String> args = new ArrayList<>(formals.length);
        for (int i = 1; i < formals.length; i++) args.add(formals[i].code);
        return String.format("(%s).%s(%s)", formals[0].code, functionName, String.join(", ", args));
    }
}
