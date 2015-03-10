package coderepair.graph;

import java.io.Serializable;

public abstract class JavaGraphNode implements Serializable {
    String name;

    public String getName() {
        return name;
    }

}

