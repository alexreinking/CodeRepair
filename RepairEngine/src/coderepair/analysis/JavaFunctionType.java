package coderepair.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavaFunctionType extends JavaType {
    protected String functionName;
    private HashMap<JavaType, Integer> inputs = new LinkedHashMap<JavaType, Integer>();
    private JavaType output;
    private int nFormals;

    public JavaFunctionType(String name, Collection<? extends JavaType> formals, JavaType output) {
        nFormals = formals.size();
        for (JavaType input : formals) inputs.put(input, 1 + inputs.getOrDefault(input, 0));
        this.output = output;
        this.functionName = name;

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
}
