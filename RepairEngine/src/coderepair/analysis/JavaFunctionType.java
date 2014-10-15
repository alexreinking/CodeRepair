package coderepair.analysis;

import java.util.*;

public class JavaFunctionType extends JavaType {
    protected String functionName;
    private final List<JavaType> signature;
    private final HashMap<JavaType, Integer> inputs = new LinkedHashMap<JavaType, Integer>();
    private final JavaType output;
    private final int nFormals;

    public JavaFunctionType(String name, Collection<? extends JavaType> formals, JavaType output) {
        nFormals = formals.size();
        for (JavaType input : formals) inputs.put(input, 1 + inputs.getOrDefault(input, 0));
        this.output = output;
        this.functionName = name;
        this.signature = new ArrayList<JavaType>(formals);

        String sep = "";
        StringBuilder args = new StringBuilder();
        for (Map.Entry<JavaType, Integer> input : inputs.entrySet()) {
            args.append(sep);
            args.append(input.getKey().getName());
            args.append(":");
            args.append(input.getValue());
            sep = ", ";
        }

        this.name = output.getName() + " " + name + "(" + args.toString() + ")";
    }

    public int getTotalFormals() {
        return nFormals;
    }

    public HashMap<JavaType, Integer> getInputs() {
        return inputs;
    }

    public JavaType getOutput() {
        return output;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<JavaType> getSignature() {
        return signature;
    }
}
