package coderepair.analysis;

public class JavaClassType extends JavaType {
    private String className;
    private String packageName;

    public JavaClassType(String qualifiedName) {
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
}
