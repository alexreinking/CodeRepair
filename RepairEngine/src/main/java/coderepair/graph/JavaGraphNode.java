package coderepair.graph;

import java.io.Serializable;

public abstract class JavaGraphNode implements Serializable {
    private final Kind kind;
    private final String name;

    protected JavaGraphNode(Kind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final Kind getKind() {
        return kind;
    }

    public enum Kind {
        ClassCast,
        Constructor,
        Method,
        StaticMethod,
        Field,
        StaticField,
        Value,
        Type
    }
}

