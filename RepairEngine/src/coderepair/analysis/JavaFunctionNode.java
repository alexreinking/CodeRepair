package coderepair.analysis;

import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.JavaFunctionSynthesizer;

import java.io.Serializable;
import java.util.*;

public class JavaFunctionNode extends JavaGraphNode implements Serializable {
    private final JavaFunctionSynthesizer synthesizer;
    private final String functionName;
    private final List<JavaTypeNode> signature;
    private final HashSet<JavaTypeNode> inputs = new HashSet<JavaTypeNode>();
    private final JavaTypeNode output;

    public JavaFunctionNode(String name, Collection<JavaTypeNode> formals,
                            JavaTypeNode output, JavaFunctionSynthesizer synthesizer) {
        StringJoiner args = new StringJoiner(" x ");
        for (JavaTypeNode formal : formals) args.add(formal.getName());
        inputs.addAll(formals);

        this.synthesizer = synthesizer;
        this.output = output;
        this.functionName = name;
        this.signature = new ArrayList<JavaTypeNode>(formals);
        this.name = String.format("%s: (%s) -> %s", this.functionName, args.toString(), output.getName());
    }

    public int getTotalFormals() {
        return signature.size();
    }

    public Set<JavaTypeNode> getInputs() {
        return inputs;
    }

    public JavaTypeNode getOutput() {
        return output;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<JavaTypeNode> getSignature() {
        return signature;
    }

    public String synthesize(CodeSnippet[] args) {
        return synthesizer.synthesizeFromArguments(functionName, args);
    }
}
