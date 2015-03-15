package coderepair.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class JavaGraphNodeFactory implements Serializable {
    private final HashMap<String, JavaTypeNode> classTypes = new HashMap<>();

    public boolean hasType(String qualifiedName) {
        return classTypes.containsKey(qualifiedName);
    }

    public JavaTypeNode getTypeByName(String qualifiedName) {
        return classTypes.computeIfAbsent(qualifiedName, name -> new JavaTypeNode(qualifiedName, true));
    }

    public JavaFunctionNode makeCastNode(JavaTypeNode lowType, JavaTypeNode highType) {
        return new JavaFunctionNode(JavaGraphNode.Kind.ClassCast, "<cast>", Arrays.asList(lowType), highType);
    }

    public JavaFunctionNode makeConstructor(JavaTypeNode type, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode(JavaGraphNode.Kind.Constructor, type.getClassName(), formals, type);
    }

    public JavaFunctionNode makeMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        ArrayList<JavaTypeNode> trueFormals = new ArrayList<>(1 + formals.size());
        trueFormals.add(owner);
        trueFormals.addAll(formals);
        return new JavaFunctionNode(JavaGraphNode.Kind.Method, name, trueFormals, output);
    }

    public JavaFunctionNode makeStaticMethod(String name, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode(JavaGraphNode.Kind.StaticMethod, name, formals, output);
    }

    public JavaFunctionNode makeValue(String value, String typeName) {
        JavaTypeNode valType = classTypes.get(typeName);
        if (valType == null) throw new RuntimeException("No such type: " + typeName);
        return new JavaFunctionNode(JavaGraphNode.Kind.Value, value, new ArrayList<>(), valType);
    }

    public JavaFunctionNode makeField(String fieldName, JavaTypeNode valType, JavaTypeNode owner) {
        return new JavaFunctionNode(JavaGraphNode.Kind.Field, fieldName, Arrays.asList(owner), valType);
    }
}
