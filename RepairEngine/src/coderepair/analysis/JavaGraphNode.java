package coderepair.analysis;

import java.io.Serializable;

/**
 * Created by alexreinking on 9/23/14.
 */
public abstract class JavaGraphNode implements Serializable {
    String name;

    public String getName() {
        return name;
    }

}

