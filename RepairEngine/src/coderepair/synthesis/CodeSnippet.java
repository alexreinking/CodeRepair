package coderepair.synthesis;

import org.jetbrains.annotations.NotNull;

/**
* Created by alex on 10/21/14.
*/
public class CodeSnippet implements Comparable<CodeSnippet> {
    public final String code;
    public final double cost;
    public final double bias;

    public CodeSnippet(String code, double cost) {
        this(code, cost, 1.0);
    }

    public CodeSnippet(String code, double cost, double bias) {
        this.code = code;
        this.cost = cost;
        this.bias = bias;
    }

    @Override
    public int compareTo(@NotNull CodeSnippet o) {
        if (cost != o.cost)
            return Double.compare(cost / bias, o.cost / o.bias);
        if (code.length() != o.code.length())
            return Integer.compare(code.length(), o.code.length());
        return code.compareTo(o.code);
    }
}
