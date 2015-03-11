package coderepair.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class JavaGraphNodeFactory implements Serializable {
    private final HashMap<String, JavaTypeNode> classTypes = new HashMap<>();

    boolean hasType(String qualifiedName) {
        return classTypes.containsKey(qualifiedName);
    }

    JavaTypeNode getTypeByName(String qualifiedName) {
        if (classTypes.containsKey(qualifiedName))
            return classTypes.get(qualifiedName);

        JavaTypeNode newType = new JavaTypeNode(qualifiedName, true);
        classTypes.put(qualifiedName, newType);
        return newType;
    }

    JavaFunctionNode makeCastNode(JavaTypeNode lowType, JavaTypeNode highType) {
        return new JavaFunctionNode(JavaGraphNode.Kind.ClassCast, "<cast>", Arrays.asList(lowType), highType);
    }

    JavaFunctionNode makeConstructor(JavaTypeNode type, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode(JavaGraphNode.Kind.Constructor, type.getClassName(), formals, type);
    }

    JavaFunctionNode makeMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        ArrayList<JavaTypeNode> trueFormals = new ArrayList<>(1 + formals.size());
        trueFormals.add(owner);
        trueFormals.addAll(formals);
        return new JavaFunctionNode(JavaGraphNode.Kind.Method, name, trueFormals, output);
    }

    JavaFunctionNode makeStaticMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode(JavaGraphNode.Kind.StaticMethod, owner.getClassName() + "." + name, formals, output);
    }

    JavaFunctionNode makeValue(String value, String typeName) {
        JavaTypeNode valType = classTypes.get(typeName);
        if (valType == null) throw new RuntimeException("No such type: " + typeName);
        return new JavaFunctionNode(JavaGraphNode.Kind.Value, value, new ArrayList<>(), valType);
    }

    JavaFunctionNode makeField(String fieldName, JavaTypeNode valType, JavaTypeNode owner) {
        return new JavaFunctionNode(JavaGraphNode.Kind.Field, fieldName, Arrays.asList(owner), valType);
    }
}
