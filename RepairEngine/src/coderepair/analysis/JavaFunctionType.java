package coderepair.analysis;

import java.util.Collection;
import java.util.HashMap;

public class JavaFunctionType extends JavaType {
    private HashMap<JavaType, Integer> inputs = new HashMap<JavaType, Integer>();
    private JavaType output;
    private int nFormals;

    public JavaFunctionType(String name, Collection<? extends JavaType> formals, JavaType output) {
        nFormals = formals.size();
        for (JavaType input : formals) inputs.put(input, 1 + inputs.getOrDefault(input, 0));
        this.output = output;

        String sep = "";
        StringBuilder args = new StringBuilder();
        for (JavaType input : formals) {
            args.append(sep);
            args.append(input.getName());
            sep = ", ";
        }

        this.name = output.getName() + " " + name + "(" + args.toString() + ")";
    }

    public int getTotalFormals() { return nFormals; }

    public HashMap<JavaType, Integer> getInputs() {
        return inputs;
    }

    public JavaType getOutput() {
        return output;
    }
}
