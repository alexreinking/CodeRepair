package coderepair;

import coderepair.analysis.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

    JavaClassType getTypeFromName(String qualifiedName) {
        if (primitiveTypes.containsKey(qualifiedName))
            return primitiveTypes.get(qualifiedName);
        else if (classTypes.containsKey(qualifiedName))
            return classTypes.get(qualifiedName);

        JavaClassType newType = new JavaClassType(qualifiedName, true);
        classTypes.put(qualifiedName, newType);
        return newType;
    }

    JavaFunctionType makeCastNode(JavaType lowType, JavaType highType) {
        return new JavaCastType(lowType, highType).setCastTarget(JavaCastType.CastTarget.SUPERCLASS);
    }

    JavaFunctionType makeInterfaceCastNode(JavaType lowType, JavaType highType) {
        return new JavaCastType(lowType, highType).setCastTarget(JavaCastType.CastTarget.INTERFACE);
    }

    JavaFunctionType makeConstructor(JavaType type, Collection<? extends JavaType> formals) {
        return new JavaFunctionType("new " + type.getName(), formals, type);
    }

    JavaFunctionType makeMethod(String name, JavaType owner, JavaType output, Collection<JavaType> formals) {
        formals.add(owner);
        return new JavaMethodType(name, formals, output).setOwner(owner);
    }

    JavaFunctionType makeStaticMethod(String name, JavaType owner, JavaType output, Collection<JavaType> formals) {
        return new JavaFunctionType(owner.getName() + "." + name, formals, output);
    }

    public JavaValueType makeValue(String value, String typeName) {
        JavaType valType = classTypes.get(typeName);
        if (valType == null) valType = primitiveTypes.get(typeName);
        if (valType == null) throw new RuntimeException("No such type: " + typeName);
        return new JavaValueType(value, valType);
    }
}
