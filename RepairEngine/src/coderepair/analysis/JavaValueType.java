package coderepair.analysis;

import java.util.ArrayList;

/**
 * Created by alexreinking on 10/6/14.
 */
public class JavaValueType extends JavaFunctionType {
    private final String name;

    public JavaValueType(String name, JavaType otherType) {
        super(name, new ArrayList<JavaType>(), otherType);
        this.name = name;
    }

    @Override public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaValueType)) return false;
        if (!super.equals(o)) return false;
        JavaValueType that = (JavaValueType) o;
        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
