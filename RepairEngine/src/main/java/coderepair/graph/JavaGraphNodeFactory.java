package coderepair.graph;

import java.io.Serializable;
import java.util.*;

import static coderepair.graph.JavaGraphNode.*;

public class JavaGraphNodeFactory implements Serializable {
    private final HashMap<String, JavaTypeNode> classTypes = new HashMap<>();

    public JavaFunctionNode makeFunctionNode(Kind kind, String name, List<JavaTypeNode> signature, JavaTypeNode outputType) {
        JavaFunctionNode fn = null;
        switch (kind) {
            case ClassCast:
                fn = makeCastNode(signature.get(0), outputType);
                break;
            case Constructor:
                fn = makeConstructor(outputType, signature);
                break;
            case Method:
                fn = makeMethod(name, signature.get(0), outputType, signature.subList(1, signature.size()));
                break;
            case StaticMethod:
                fn = makeStaticMethod(name, outputType, signature);
                break;
            case Field:
                fn = makeField(name, outputType, signature.size() > 0 ? signature.get(0) : null);
                break;
            case StaticField:
                fn = makeField(name, outputType, signature.size() > 0 ? signature.get(0) : null);
                break;
            case Value:
                fn = makeValue(name, outputType.getName());
                break;
        }
        return fn;
    }

    public boolean hasType(String qualifiedName) {
        return classTypes.containsKey(qualifiedName);
    }

    public JavaTypeNode getTypeByName(String qualifiedName) {
        return classTypes.computeIfAbsent(qualifiedName, name -> new JavaTypeNode(qualifiedName));
    }

    public JavaFunctionNode makeCastNode(JavaTypeNode lowType, JavaTypeNode highType) {
        return new JavaFunctionNode(Kind.ClassCast, "<cast>", Collections.singletonList(lowType), highType);
    }

    public JavaFunctionNode makeConstructor(JavaTypeNode type, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode(Kind.Constructor, type.getClassName(), formals, type);
    }

    public JavaFunctionNode makeMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        ArrayList<JavaTypeNode> trueFormals = new ArrayList<>(1 + formals.size());
        trueFormals.add(owner);
        trueFormals.addAll(formals);
        return new JavaFunctionNode(Kind.Method, name, trueFormals, output);
    }

    public JavaFunctionNode makeStaticMethod(String name, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode(Kind.StaticMethod, name, formals, output);
    }

    public JavaFunctionNode makeValue(String value, String typeName) {
        JavaTypeNode valType = classTypes.get(typeName);
        if (valType == null) throw new RuntimeException("No such type: " + typeName);
        return new JavaFunctionNode(Kind.Value, value, new ArrayList<>(), valType);
    }

    public JavaFunctionNode makeField(String fieldName, JavaTypeNode valType, JavaTypeNode owner) {
        return new JavaFunctionNode(Kind.Field, fieldName, Collections.singletonList(owner), valType);
    }
}
