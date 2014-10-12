package coderepair.analysis;

public class JavaClassType extends JavaType {
    private String className;
    private String packageName;
    private boolean conrete;

    public JavaClassType(String qualifiedName, boolean conrete) {
        this.conrete = conrete;
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

    public void setConrete(boolean conrete) {
        this.conrete = conrete;
    }

    public boolean isConcrete() {
        return conrete;
    }
}
