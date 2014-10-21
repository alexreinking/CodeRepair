package coderepair.analysis;

public class JavaClassType extends JavaType {
    private final String className;
    private final String packageName;
    private boolean concrete;

    public JavaClassType(String qualifiedName, boolean concrete) {
        this.concrete = concrete;
        int lastSeparator = Math.max(qualifiedName.lastIndexOf('.'), qualifiedName.lastIndexOf('/'));
        lastSeparator = Math.max(0, lastSeparator);

        this.name = qualifiedName;
        this.className = qualifiedName.substring(lastSeparator + 1);
        this.packageName = qualifiedName.substring(0, lastSeparator);
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
}
