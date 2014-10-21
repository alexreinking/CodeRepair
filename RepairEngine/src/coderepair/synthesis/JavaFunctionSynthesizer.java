package coderepair.synthesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 10/21/14.
 */
public abstract class JavaFunctionSynthesizer implements Serializable {
    public abstract String synthesizeFromArguments(String functionName, List<CodeSnippet> formals);
    public String synthesizeFromArguments(String functionName, CodeSnippet[] formals) {
        return synthesizeFromArguments(functionName, new ArrayList<CodeSnippet>(Arrays.asList(formals)));
    }
}
