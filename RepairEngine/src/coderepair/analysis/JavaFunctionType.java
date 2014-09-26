package coderepair.analysis;
import java.util.HashSet;

public class JavaFunctionType extends JavaType {
    private HashSet<JavaType> inputs;
    private JavaType output;

    public JavaFunctionType(String name, HashSet<JavaType> inputs, JavaType output) {
        this.inputs = inputs;
        this.output = output;

        String sep = "";
        StringBuilder args = new StringBuilder();
        for (JavaType input : inputs) {
            args.append(sep);
            args.append(input.getName());
            sep = ", ";
        }

        this.name = output.getName() + " " + name + "(" + args.toString() + ")";
    }

    public HashSet<JavaType> getInputs() {
        return inputs;
    }

    public JavaType getOutput() {
        return output;
    }
}
