package coderepair.synthesis;

import coderepair.SynthesisGraph;
import org.jetbrains.annotations.NotNull;

/**
* Created by alex on 10/21/14.
*/
public class Snippet implements Comparable<Snippet> {
    public final String code;
    public final double cost;

    public Snippet(String code, double cost) {
        this.code = code;
        this.cost = cost;
    }

    @Override
    public int compareTo(@NotNull Snippet o) {
        if (cost != o.cost)
            return Double.compare(cost, o.cost);
        if (code.length() != o.code.length())
            return Integer.compare(code.length(), o.code.length());
        return code.compareTo(o.code);
    }
}
