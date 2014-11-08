package coderepair;

import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaTypeNode;
import coderepair.synthesis.types.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

class JavaTypeBuilder implements Serializable {
    private final HashMap<String, JavaTypeNode> classTypes = new HashMap<>();

    JavaTypeNode getTypeFromName(String qualifiedName) {
        if (classTypes.containsKey(qualifiedName))
            return classTypes.get(qualifiedName);

        JavaTypeNode newType = new JavaTypeNode(qualifiedName, true);
        classTypes.put(qualifiedName, newType);
        return newType;
    }

    JavaFunctionNode makeCastNode(JavaTypeNode lowType, JavaTypeNode highType) {
        return new JavaFunctionNode("<cast>", Arrays.asList(lowType), highType, new CastSynthesizer());
    }

    JavaFunctionNode makeConstructor(JavaTypeNode type, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode("new " + type.getName(), formals, type, new StaticFunctionSynthesizer());
    }

    JavaFunctionNode makeMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        ArrayList<JavaTypeNode> trueFormals = new ArrayList<>(1 + formals.size());
        trueFormals.add(owner);
        trueFormals.addAll(formals);
        return new JavaFunctionNode(owner.getName() + "." + name, trueFormals, output, new MethodSynthesizer());
    }

    JavaFunctionNode makeStaticMethod(String name, JavaTypeNode owner, JavaTypeNode output, Collection<JavaTypeNode> formals) {
        return new JavaFunctionNode(owner.getName() + "." + name, formals, output, new StaticFunctionSynthesizer());
    }

    JavaFunctionNode makeValue(String value, String typeName) {
        JavaTypeNode valType = classTypes.get(typeName);
        if (valType == null) throw new RuntimeException("No such type: " + typeName);
        return new JavaFunctionNode(value, new ArrayList<>(), valType, new ValueSynthesizer());
    }

    JavaFunctionNode makeField(String fieldName, JavaTypeNode valType, JavaTypeNode owner) {
        return new JavaFunctionNode(fieldName, Arrays.asList(owner), valType, new FieldSynthesizer());
    }
}
