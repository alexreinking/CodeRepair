package coderepair;

import coderepair.analysis.JavaClassType;
import coderepair.analysis.JavaFunctionType;
import coderepair.analysis.JavaPrimitiveType;
import coderepair.analysis.JavaType;

import java.util.*;

public class JavaTypeBuilder {
    private HashMap<String, JavaPrimitiveType> primitiveTypes = new HashMap<String, JavaPrimitiveType>();
    private HashMap<String, JavaClassType> classTypes = new HashMap<String, JavaClassType>();

    public JavaTypeBuilder() {
        List<String> primNames = Arrays.asList("byte", "short", "int", "long", "float", "double", "boolean", "char");
        for (String primType : primNames) {
            primitiveTypes.put(primType, new JavaPrimitiveType(primType));
            primitiveTypes.put(primType + "[]", new JavaPrimitiveType(primType + "[]"));
        }
    }

    JavaType getTypeFromName(String qualifiedName) {
        if (primitiveTypes.containsKey(qualifiedName))
            return primitiveTypes.get(qualifiedName);
        else if (classTypes.containsKey(qualifiedName))
            return classTypes.get(qualifiedName);

        JavaClassType newType = new JavaClassType(qualifiedName);
        classTypes.put(qualifiedName, newType);
        return newType;
    }

    JavaFunctionType makeCastNode(JavaType lowType, JavaType highType) {
        return new JavaFunctionType("<cast>", new HashSet<JavaType>(Arrays.asList(lowType)), highType);
    }

    JavaFunctionType makeConstructor(JavaType type, Collection<? extends JavaType> formals) {
        return new JavaFunctionType("new " + type.getName(), new HashSet<JavaType>(formals), type);
    }

    JavaFunctionType makeMethod(String name, JavaType type, JavaType output, Collection<? extends JavaType> formals) {
        HashSet<JavaType> inputs = new HashSet<JavaType>(formals);
        inputs.add(type);
        return new JavaFunctionType(name, inputs, output);
    }
}
