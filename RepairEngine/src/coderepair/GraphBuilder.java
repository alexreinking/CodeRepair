package coderepair;

import coderepair.analysis.JavaClassType;
import coderepair.analysis.JavaFunctionType;
import coderepair.analysis.JavaPrimitiveType;
import coderepair.analysis.JavaType;
import coderepair.antlr.JavaPBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static coderepair.antlr.JavaPParser.*;

public class GraphBuilder extends JavaPBaseVisitor<SynthesisGraph> {
    private SynthesisGraph fnFlowGraph = null;
    private HashSet<String> allowedPackages = new HashSet<String>();
    private HashSet<JavaFunctionType> methods = new HashSet<JavaFunctionType>();

    public GraphBuilder() {
        this(new ArrayList<String>());
    }

    public GraphBuilder(List<String> packages) {
        allowedPackages.addAll(packages);
        allowedPackages.add("java.lang");
    }

    @Override
    public SynthesisGraph visitJavap(@NotNull JavapContext ctx) {
        fnFlowGraph = new SynthesisGraph(new JavaTypeBuilder());
        for (ClassDeclarationContext classDeclarationContext : ctx.classDeclaration())
            visitClassDeclaration(classDeclarationContext);

        for (JavaFunctionType method : methods) {
            fnFlowGraph.addVertex(method);
            fnFlowGraph.setEdgeWeight(fnFlowGraph.addEdge(method.getOutput(), method),
                                            method.getName().contains("<cast>")
                                                    ? 0.0
                                                    : Math.pow(1.5, method.getTotalFormals()));

            for (JavaType inType : method.getInputs().keySet())
                fnFlowGraph.addEdge(method, inType);
        }

        return fnFlowGraph;
    }

    @Override
    public SynthesisGraph visitClassDeclaration(@NotNull ClassDeclarationContext ctx) {
        JavaType classNode = fnFlowGraph.getNodeManager().getTypeFromName(ctx.typeName().getText());
        if (addTypeToGraph(classNode)) {
            List<TypeNameContext> superTypes = new ArrayList<TypeNameContext>();
            if (ctx.extension() != null && ctx.extension().typeList() != null)
                superTypes.addAll(ctx.extension().typeList().typeName());
            if (ctx.implementation() != null && ctx.implementation().typeList() != null)
                superTypes.addAll(ctx.implementation().typeList().typeName());

            for (TypeNameContext parentType : superTypes) {
                JavaType parentNode = fnFlowGraph.getNodeManager().getTypeFromName(parentType.getText());
                if (addTypeToGraph(parentNode))
                    methods.add(fnFlowGraph.getNodeManager().makeCastNode(classNode, parentNode));
            }

            for (MemberDeclarationContext memberDeclarationContext : ctx.memberDeclaration()) {
                MethodDeclarationContext method = memberDeclarationContext.methodDeclaration();
                if (method != null) {
                    boolean badFormal = false;
                    List<JavaType> formals = new ArrayList<JavaType>();
                    if (method.typeList() != null) {
                        for (TypeNameContext formal : method.typeList().typeName()) {
                            JavaType formalType = fnFlowGraph.getNodeManager().getTypeFromName(formal.getText());
                            if (!addTypeToGraph(formalType)) {
                                badFormal = true;
                                break;
                            }

                            formals.add(formalType);
                        }
                    }

                    if (badFormal) continue;

                    if (method.typeName().size() == 1) {
                        methods.add(fnFlowGraph.getNodeManager().makeConstructor(classNode, formals));
                    } else {
                        JavaType outType = fnFlowGraph.getNodeManager().getTypeFromName(method.typeName().get(0).getText());
                        if (addTypeToGraph(outType)) {
                            String methodName = method.typeName().get(1).getText();
                            JavaFunctionType newFunc;
                            if(method.modifiers().STATIC() != null)
                                newFunc = fnFlowGraph.getNodeManager().makeStaticMethod(methodName, classNode, outType, formals);
                            else
                                newFunc = fnFlowGraph.getNodeManager().makeMethod(methodName, classNode, outType, formals);
                            methods.add(newFunc);
                        }
                    }
                } else if (memberDeclarationContext.fieldDeclaration() != null) {
                    // TODO: fill in
                } else {
                    System.err.println("Warning: unknown class member " + memberDeclarationContext.getText());
                }
            }

        }

        return fnFlowGraph;
    }

    private boolean addTypeToGraph(JavaType type) {
        if (type.getName().contains("$"))
            return false;
        if (type instanceof JavaPrimitiveType) {
            fnFlowGraph.addVertex(type);
            return true;
        } else if (type instanceof JavaClassType) {
            String packageName = ((JavaClassType) type).getPackageName();
            for (String okPackage : allowedPackages)
                if (packageName.startsWith(okPackage)) {
                    fnFlowGraph.addVertex(type);
                    return true;
                }
        }
        return false;
    }
}