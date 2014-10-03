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
    private SynthesisGraph functionFlowGraph = null;
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
        functionFlowGraph = new SynthesisGraph(new JavaTypeBuilder());
        for (ClassDeclarationContext classDeclarationContext : ctx.classDeclaration())
            visitClassDeclaration(classDeclarationContext);

        for (JavaFunctionType method : methods) {
            functionFlowGraph.addVertex(method);
            functionFlowGraph.setEdgeWeight(functionFlowGraph.addEdge(method.getOutput(), method),
                                            method.getName().equals("<cast>")
                                                    ? 0.0
                                                    : method.getTotalFormals() * 2.0);

            for (JavaType inType : method.getInputs().keySet())
                functionFlowGraph.addEdge(method, inType);
        }

        return functionFlowGraph;
    }

    @Override
    public SynthesisGraph
    visitClassDeclaration(@NotNull ClassDeclarationContext ctx) {
        JavaType classNode = functionFlowGraph.getNodeManager().getTypeFromName(ctx.typeName().getText());
        if (addTypeToGraph(classNode)) {
            List<TypeNameContext> superTypes = new ArrayList<TypeNameContext>();
            if (ctx.extension() != null && ctx.extension().typeList() != null)
                superTypes.addAll(ctx.extension().typeList().typeName());
            if (ctx.implementation() != null && ctx.implementation().typeList() != null)
                superTypes.addAll(ctx.implementation().typeList().typeName());

            for (TypeNameContext parentType : superTypes) {
                JavaType parentNode = functionFlowGraph.getNodeManager().getTypeFromName(parentType.getText());
                if (addTypeToGraph(parentNode))
                    methods.add(functionFlowGraph.getNodeManager().makeCastNode(classNode, parentNode));
            }

            for (MemberDeclarationContext memberDeclarationContext : ctx.memberDeclaration()) {
                MethodDeclarationContext method = memberDeclarationContext.methodDeclaration();
                if (method != null) {
                    boolean badFormal = false;
                    List<JavaType> formals = new ArrayList<JavaType>();
                    if (method.typeList() != null) {
                        for (TypeNameContext formal : method.typeList().typeName()) {
                            JavaType formalType = functionFlowGraph.getNodeManager().getTypeFromName(formal.getText());
                            if (!addTypeToGraph(formalType)) {
                                badFormal = true;
                                break;
                            }

                            formals.add(formalType);
                        }
                    }

                    if (badFormal) continue;

                    if (method.typeName().size() == 1) {
                        methods.add(functionFlowGraph.getNodeManager().makeConstructor(classNode, formals));
                    } else {
                        JavaType outType = functionFlowGraph.getNodeManager().getTypeFromName(method.typeName().get(0).getText());
                        if (addTypeToGraph(outType)) {
                            String methodName = method.typeName().get(1).getText();
                            methods.add(functionFlowGraph.getNodeManager().makeMethod(methodName, classNode, outType, formals));
                        }
                    }
                } else if (memberDeclarationContext.fieldDeclaration() != null) {
                    // TODO: fill in
                } else {
                    System.err.println("Warning: unknown class member " + memberDeclarationContext.getText());
                }
            }

        }

        return functionFlowGraph;
    }

    private boolean addTypeToGraph(JavaType type) {
        if (type.getName().contains("$"))
            return false;
        if (type instanceof JavaPrimitiveType) {
            functionFlowGraph.addVertex(type);
            return true;
        } else if (type instanceof JavaClassType) {
            String packageName = ((JavaClassType) type).getPackageName();
            for (String okPackage : allowedPackages)
                if (packageName.startsWith(okPackage)) {
                    functionFlowGraph.addVertex(type);
                    return true;
                }
        }
        return false;
    }
}