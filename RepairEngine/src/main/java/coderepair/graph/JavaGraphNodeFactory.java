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
        return new JavaFunctionNode("<cast>", Arrays.asList(lowType), highType, JavaFunctionNode.Role.ClassCast);
    }

    JavaFunctionNode makeConstructor(JavaTypeNode type, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode("new " + type.getClassName(), formals, type, JavaFunctionNode.Role.Constructor);
    }

    JavaFunctionNode makeMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        ArrayList<JavaTypeNode> trueFormals = new ArrayList<>(1 + formals.size());
        trueFormals.add(owner);
        trueFormals.addAll(formals);
        return new JavaFunctionNode(name, trueFormals, output, JavaFunctionNode.Role.Method);
    }

    JavaFunctionNode makeStaticMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        JavaFunctionNode node = new JavaFunctionNode(owner.getClassName() + "." + name, formals, output, JavaFunctionNode.Role.Method);
        node.setStatic(true);
        return node;
    }

    JavaFunctionNode makeValue(String value, String typeName) {
        JavaTypeNode valType = classTypes.get(typeName);
        if (valType == null) throw new RuntimeException("No such type: " + typeName);
        return new JavaFunctionNode(value, new ArrayList<>(), valType, JavaFunctionNode.Role.Value);
    }

    JavaFunctionNode makeField(String fieldName, JavaTypeNode valType, JavaTypeNode owner) {
        JavaFunctionNode javaFunctionNode = new JavaFunctionNode(fieldName, Arrays.asList(owner), valType, JavaFunctionNode.Role.Value);
        javaFunctionNode.setStatic(true);
        return javaFunctionNode;
    }
}
