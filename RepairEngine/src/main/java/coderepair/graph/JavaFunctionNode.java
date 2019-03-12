package coderepair.graph;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class JavaFunctionNode extends JavaGraphNode implements Serializable {
    private final String functionName;
    private final List<JavaTypeNode> signature;
    private final HashSet<JavaTypeNode> inputs;
    private final JavaTypeNode output;

    public JavaFunctionNode(Kind kind, String name, Collection<JavaTypeNode> formals, JavaTypeNode output) {
        super(kind, String.format("%s: (%s) -> %s", name,
                formals.stream().map(JavaTypeNode::getName).collect(Collectors.joining(" x ")),
                output.getName()));

        this.inputs = new HashSet<>(formals);
        this.output = output;
        this.functionName = name;
        this.signature = new ArrayList<>(formals);
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

}
