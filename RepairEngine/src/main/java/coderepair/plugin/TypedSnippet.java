package coderepair.plugin;

class TypedSnippet {
    public final String type;
    public final String code;

    TypedSnippet(String code, String type) {
        this.code = code;
        this.type = type;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedSnippet that = (TypedSnippet) o;

        return code.equals(that.code) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }
}
