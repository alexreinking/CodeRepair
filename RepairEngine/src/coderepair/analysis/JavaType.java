package coderepair.analysis;

/**
 * Created by alexreinking on 9/23/14.
 */
public abstract class JavaType {
    protected String name;

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof JavaType && name.equals(((JavaType) o).name);
    }
}

