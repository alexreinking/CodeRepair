package coderepair.synthesis;

import coderepair.graph.JavaFunctionNode;

import java.util.ArrayList;

/**
 * Created by alexreinking on 3/10/15.
 */
public class SnippetBuilder {
    public static String buildInvocation(JavaFunctionNode fn, CodeSnippet[] args) {
        ArrayList<String> formals = new ArrayList<>(args.length);
        String functionName = fn.getFunctionName();
        String snippet = "";

        switch (fn.getKind()) {
            case ClassCast:
                if (args.length != 1)
                    throw new IllegalArgumentException("Error! Can only cast exactly one type to another");
                snippet = args[0].code;
                break;

            case Constructor:
                for (CodeSnippet arg : args) formals.add(arg.code);
                snippet = String.format("new %s(%s)", functionName, String.join(", ", formals));
                break;

            case Method:
                if (fn.isStatic()) {
                    for (CodeSnippet arg : args) formals.add(arg.code);
                    snippet = String.format("%s(%s)", functionName, String.join(", ", formals));
                } else {
                    for (int i = 1; i < args.length; i++) formals.add(args[i].code);
                    snippet = String.format("(%s).%s(%s)", args[0].code, functionName, String.join(", ", formals));
                }
                break;

            case Value:
                if (fn.isStatic()) {
                    if (args.length != 1)
                        throw new IllegalArgumentException("Methods must at least have an owner, and cannot take arguments.");
                    snippet = String.format("(%s).%s", args[0].code, functionName);
                } else {
                    if (args.length != 0)
                        throw new IllegalArgumentException("Values may not take arguments.");
                    snippet = functionName;
                }
                break;
        }
        return snippet;
    }
}
