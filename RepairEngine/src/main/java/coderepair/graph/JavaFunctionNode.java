package coderepair.graph;

import java.io.Serializable;
import java.util.*;

public class JavaFunctionNode extends JavaGraphNode implements Serializable {

    private final String functionName;
    private final List<JavaTypeNode> signature;
    private final HashSet<JavaTypeNode> inputs = new HashSet<>();
    private final JavaTypeNode output;
    private final Kind kind;
    private final boolean isStatic;

    public JavaFunctionNode(Kind kind, String name, Collection<JavaTypeNode> formals,
                            JavaTypeNode output, boolean isStatic) {
        StringJoiner args = new StringJoiner(" x ");
        for (JavaTypeNode formal : formals) args.add(formal.getName());
        inputs.addAll(formals);

        this.kind = kind;
        this.output = output;
        this.functionName = name;
        this.signature = new ArrayList<>(formals);
        this.name = String.format("%s: (%s) -> %s", this.functionName, args.toString(), output.getName());
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
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

    public Kind getKind() {
        return kind;
    }

    public enum Kind {
        ClassCast,
        Constructor,
        Method,
        Value
    }
}
