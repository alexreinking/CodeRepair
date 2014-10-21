package coderepair.synthesis;

import org.jetbrains.annotations.NotNull;

/**
* Created by alex on 10/21/14.
*/
public class CodeSnippet implements Comparable<CodeSnippet> {
    public final String code;
    public final double cost;

    public CodeSnippet(String code, double cost) {
        this.code = code;
        this.cost = cost;
    }

    @Override
    public int compareTo(@NotNull CodeSnippet o) {
        if (cost != o.cost)
            return Double.compare(cost, o.cost);
        if (code.length() != o.code.length())
            return Integer.compare(code.length(), o.code.length());
        return code.compareTo(o.code);
    }
}
