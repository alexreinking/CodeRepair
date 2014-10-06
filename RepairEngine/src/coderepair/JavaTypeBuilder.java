package coderepair;

import coderepair.analysis.*;

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
        return new JavaFunctionType("<cast>", Arrays.asList(lowType), highType);
    }

    JavaFunctionType makeConstructor(JavaType type, Collection<? extends JavaType> formals) {
        return new JavaFunctionType("new " + type.getName(), formals, type);
    }

    JavaFunctionType makeMethod(String name, JavaType type, JavaType output, Collection<JavaType> formals) {
        return new JavaMethodType(name, formals, output).setOwner(type);
    }

    JavaFunctionType makeStaticMethod(String name, JavaType type, JavaType output, Collection<JavaType> formals) {
        return new JavaFunctionType(type.getName() + "." + name, formals, output);
    }

    public JavaValueType makeValue(String value, String typeName) {
        return new JavaValueType(value, classTypes.get(typeName));
    }
}
