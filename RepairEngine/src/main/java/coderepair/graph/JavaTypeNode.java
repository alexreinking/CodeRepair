package coderepair.graph;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTypeNode extends JavaGraphNode implements Serializable {
    private final String className;
    private final String packageName;
    private final boolean primitive;
    private boolean concrete; // TODO: make final

    public JavaTypeNode(String qualifiedName) {
        super(Kind.Type, qualifiedName);
        this.concrete = true;

        Pattern packageClassSep = Pattern.compile("((?:\\w+\\.)+)?(.+)");
        Matcher matcher = packageClassSep.matcher(getName());

        if (matcher.find()) {
            this.className = matcher.group(2);
            if (matcher.group(1) != null)
                this.packageName = matcher.group(1).substring(0, matcher.group(1).length() - 1);
            else this.packageName = "";
        } else throw new IllegalArgumentException(String.format("'%s' is not a valid Java identifier.", qualifiedName));

        this.primitive = this.packageName.isEmpty() || this.packageName.startsWith("java.lang");
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isConcrete() {
        return concrete;
    }

    public void makeAbstract() {
        this.concrete = false;
    }

    public boolean isPrimitive() {
        return primitive;
    }

}
