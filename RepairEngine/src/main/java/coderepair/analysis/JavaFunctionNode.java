package coderepair.analysis;

import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.types.JavaFunctionSynthesizer;

import java.io.Serializable;
import java.util.*;

public class JavaFunctionNode extends JavaGraphNode implements Serializable {
    private final JavaFunctionSynthesizer synthesizer;
    private final String functionName;
    private final List<JavaTypeNode> signature;
    private final HashSet<JavaTypeNode> inputs = new HashSet<>();
    private final JavaTypeNode output;
    private boolean isStatic = false;

    public JavaFunctionNode(String name, Collection<JavaTypeNode> formals,
                            JavaTypeNode output, JavaFunctionSynthesizer synthesizer) {
        StringJoiner args = new StringJoiner(" x ");
        for (JavaTypeNode formal : formals) args.add(formal.getName());
        inputs.addAll(formals);

        this.synthesizer = synthesizer;
        this.output = output;
        this.functionName = name;
        this.signature = new ArrayList<>(formals);
        this.name = String.format("%s: (%s) -> %s", this.functionName, args.toString(), output.getName());
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
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