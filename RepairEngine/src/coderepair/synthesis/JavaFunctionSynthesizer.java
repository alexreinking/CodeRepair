package coderepair.synthesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class JavaFunctionSynthesizer implements Serializable {
    public abstract String synthesizeFromArguments(String functionName, List<CodeSnippet> formals);
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals) {
        return synthesizeFromArguments(functionName, new ArrayList<>(Arrays.asList(formals)));
    }
}
