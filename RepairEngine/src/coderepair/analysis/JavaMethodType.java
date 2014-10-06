package coderepair.analysis;

import java.util.Collection;

/**
 * Created by alexreinking on 10/6/14.
 */
public class JavaMethodType extends JavaFunctionType {
    public JavaType owner = null;

    public JavaMethodType(String name, Collection<? extends JavaType> formals, JavaType output) {
        super(name, formals, output);
    }

    public JavaType getOwner() {
        return owner;
    }

    public JavaMethodType setOwner(JavaType owner) {
        this.owner = owner;
        return this;
    }
}
