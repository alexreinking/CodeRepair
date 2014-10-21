package coderepair.analysis;

public class JavaTypeNode extends JavaGraphNode {
    private final String className;
    private final String packageName;
    private boolean concrete;
    private boolean primitive;

    public JavaTypeNode(String qualifiedName, boolean concrete) {
        this.concrete = concrete;
        this.name = qualifiedName;
        int lastSeparator = Math.max(qualifiedName.lastIndexOf('.'), qualifiedName.lastIndexOf('/'));
        if (lastSeparator == -1) {
            this.className = qualifiedName;
            this.packageName = "";
            this.primitive = true;
        } else {
            this.className = qualifiedName.substring(lastSeparator + 1);
            this.packageName = qualifiedName.substring(0, lastSeparator);
            this.primitive = false;
        }
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setConcrete(boolean concrete) {
        this.concrete = concrete;
    }

    public boolean isConcrete() {
        return concrete;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }
}
