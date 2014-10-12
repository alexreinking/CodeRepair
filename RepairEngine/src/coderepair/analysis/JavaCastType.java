package coderepair.analysis;

import java.util.Arrays;

public class JavaCastType extends JavaFunctionType {
    private CastTarget castTarget = CastTarget.SUPERCLASS;

    public CastTarget getCastTarget() {
        return castTarget;
    }

    public JavaCastType setCastTarget(CastTarget castTarget) {
        this.castTarget = castTarget;
        this.functionName = this.castTarget == CastTarget.INTERFACE ? "<i-cast>" : "<s-cast>";
        return this;
    }

    public enum CastTarget {
        SUPERCLASS,
        INTERFACE
    }

    public JavaCastType(JavaType lowType, JavaType highType) {
        super("<cast>", Arrays.asList(lowType), highType);
    }
}
