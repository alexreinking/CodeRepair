package coderepair;

import coderepair.analysis.*;
import coderepair.antlr.JavaPBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static coderepair.antlr.JavaPParser.*;

public class GraphBuilder extends JavaPBaseVisitor<SynthesisGraph> {
    private final HashSet<String> allowedPackages = new HashSet<String>();
    private final HashSet<JavaFunctionType> methods = new HashSet<JavaFunctionType>();
    private final double costLimit;
    private SynthesisGraph fnFlowGraph = null;

    public GraphBuilder() {
        this(new ArrayList<String>(), 10.0);
    }

    public GraphBuilder(double costLimit) {
        this(new ArrayList<String>(), costLimit);
    }

    public GraphBuilder(List<String> packages) {
        this(packages, 10.0);
    }

    public GraphBuilder(List<String> packages, double costLimit) {
        this.costLimit = costLimit;
        allowedPackages.addAll(packages);
        allowedPackages.add("java.lang");
    }

    private double costForFunction(JavaFunctionType method) {
        if (method instanceof JavaCastType)
            return ((JavaCastType) method).getCastTarget() == JavaCastType.CastTarget.INTERFACE ? 0.0 : 0.5;
        if (method.getFunctionName().startsWith("new "))
            return 2 + method.getTotalFormals();
        return 1 + method.getTotalFormals();
    }

    @Override
    public SynthesisGraph visitJavap(@NotNull JavapContext ctx) {
        fnFlowGraph = new SynthesisGraph(new JavaTypeBuilder(), costLimit);
        for (ClassDeclarationContext classDeclarationContext : ctx.classDeclaration())
            visitClassDeclaration(classDeclarationContext);

        for (JavaFunctionType method : methods) {
            fnFlowGraph.addVertex(method);
            fnFlowGraph.setEdgeWeight(fnFlowGraph.addEdge(method.getOutput(), method), costForFunction(method));

            for (JavaType inType : method.getInputs().keySet())
                fnFlowGraph.setEdgeWeight(fnFlowGraph.addEdge(method, inType), 0.0);
        }

        return fnFlowGraph;
    }

    @Override
    public SynthesisGraph visitClassDeclaration(@NotNull ClassDeclarationContext ctx) {
        JavaClassType classNode = fnFlowGraph.getNodeManager().getTypeFromName(ctx.typeName().getText());
        if (ctx.INTERFACE() != null || ctx.modifiers() != null && ctx.modifiers().ABSTRACT() != null)
            classNode.setConcrete(false);

        if (addTypeToGraph(classNode)) {
            List<TypeNameContext> superTypes = new ArrayList<TypeNameContext>();
            if (ctx.extension() != null && ctx.extension().typeList() != null)
                superTypes.addAll(ctx.extension().typeList().typeName());
            if (ctx.implementation() != null && ctx.implementation().typeList() != null)
                superTypes.addAll(ctx.implementation().typeList().typeName());

            for (TypeNameContext parentType : superTypes) {
                JavaClassType parentNode = fnFlowGraph.getNodeManager().getTypeFromName(parentType.getText());
                if (addTypeToGraph(parentNode)) {
                    if (parentNode.isConcrete())
                        methods.add(fnFlowGraph.getNodeManager().makeCastNode(classNode, parentNode));
                    else
                        methods.add(fnFlowGraph.getNodeManager().makeInterfaceCastNode(classNode, parentNode));
                }
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
                            if (method.modifiers().STATIC() != null)
                                newFunc = fnFlowGraph.getNodeManager().makeStaticMethod(methodName, classNode, outType, formals);
                            else
                                newFunc = fnFlowGraph.getNodeManager().makeMethod(methodName, classNode, outType, formals);
                            methods.add(newFunc);
                        }
                    }
                } else if (memberDeclarationContext.fieldDeclaration() != null) {
                    // TODO: fill in for public constants
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
                if (packageName.startsWith(okPackage) || allowedPackages.size() == 1) {
                    fnFlowGraph.addVertex(type);
                    return true;
                }
        }
        return false;
    }
}