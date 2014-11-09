package coderepair;

import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaGraphNode;
import coderepair.analysis.JavaTypeNode;
import coderepair.antlr.JavaPBaseVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static coderepair.antlr.JavaPParser.*;

public class GraphBuilder extends JavaPBaseVisitor<SynthesisGraph> {
    private final HashSet<String> allowedPackages = new HashSet<>();
    private final HashSet<JavaFunctionNode> methods = new HashSet<>();
    private SynthesisGraph fnFlowGraph = null;
    private JavaTypeBuilder nodeManager;

    public GraphBuilder() {
        this(new ArrayList<>());
    }

    public GraphBuilder(List<String> packages) {
        allowedPackages.addAll(packages);
        allowedPackages.add("java.lang");
    }

    private double costForFunction(JavaFunctionNode method) {
        if (method.getFunctionName().equals("<cast>"))
            return method.getOutput().isConcrete() ? 0.0 : 0.0;
        return 1 + method.getTotalFormals();
    }

    @Override
    public SynthesisGraph visitJavap(@NotNull JavapContext ctx) {
        nodeManager = new JavaTypeBuilder();
        fnFlowGraph = new SynthesisGraph(nodeManager);

        List<String> primNames = Arrays.asList("byte", "short", "int", "long", "float", "double", "boolean", "char");
        for (String primType : primNames) {
            addTypeToGraph(nodeManager.getTypeFromName(primType));
            addTypeToGraph(nodeManager.getTypeFromName(primType + "[]"));
        }

        ctx.classDeclaration().forEach(this::visitClassDeclaration);

        for (JavaFunctionNode method : methods) {
            fnFlowGraph.addVertex(method);
            fnFlowGraph.setEdgeWeight(fnFlowGraph.addEdge(method.getOutput(), method), costForFunction(method));

            for (JavaGraphNode inType : method.getInputs())
                fnFlowGraph.setEdgeWeight(fnFlowGraph.addEdge(method, inType), 0.0);
        }

        return fnFlowGraph;
    }

    @Override
    public SynthesisGraph visitClassDeclaration(@NotNull ClassDeclarationContext ctx) {
        JavaTypeNode classNode = nodeManager.getTypeFromName(ctx.typeName().getText());
        if (ctx.INTERFACE() != null || ctx.modifiers() != null && ctx.modifiers().ABSTRACT() != null)
            classNode.setConcrete(false);

        if (addTypeToGraph(classNode)) {
            List<TypeNameContext> superTypes = new ArrayList<>();
            if (ctx.extension() != null && ctx.extension().typeList() != null)
                superTypes.addAll(ctx.extension().typeList().typeName());
            if (ctx.implementation() != null && ctx.implementation().typeList() != null)
                superTypes.addAll(ctx.implementation().typeList().typeName());

            for (TypeNameContext parentType : superTypes) {
                JavaTypeNode parentNode = nodeManager.getTypeFromName(parentType.getText());
                if (addTypeToGraph(parentNode))
                    methods.add(nodeManager.makeCastNode(classNode, parentNode));
            }

            for (MemberDeclarationContext memberDeclarationContext : ctx.memberDeclaration()) {
                MethodDeclarationContext method = memberDeclarationContext.methodDeclaration();
                if (method != null) {
                    boolean badFormal = false;
                    List<JavaTypeNode> formals = new ArrayList<>();
                    if (method.typeList() != null) {
                        for (TypeNameContext formal : method.typeList().typeName()) {
                            JavaTypeNode formalType = nodeManager.getTypeFromName(formal.getText());
                            if (!addTypeToGraph(formalType)) {
                                badFormal = true;
                                break;
                            }

                            formals.add(formalType);
                        }
                    }

                    if (badFormal) continue;

                    if (method.typeName().size() == 1) {
                        methods.add(nodeManager.makeConstructor(classNode, formals));
                    } else {
                        JavaTypeNode outType = nodeManager.getTypeFromName(method.typeName().get(0).getText());
                        if (addTypeToGraph(outType)) {
                            String methodName = method.typeName().get(1).getText();
                            JavaFunctionNode newFunc;
                            if (method.modifiers().STATIC() != null)
                                newFunc = nodeManager.makeStaticMethod(methodName, classNode, outType, formals);
                            else
                                newFunc = nodeManager.makeMethod(methodName, classNode, outType, formals);
                            methods.add(newFunc);
                        }
                    }
                } else if (memberDeclarationContext.fieldDeclaration() != null
                        && memberDeclarationContext.fieldDeclaration().modifiers().STATIC() != null) {
                    String typeName = memberDeclarationContext.fieldDeclaration().typeName().getText();
                    String valueName = classNode.getName() + "." + memberDeclarationContext.fieldDeclaration().identifier().getText();
                    JavaTypeNode valueType = nodeManager.getTypeFromName(typeName);
                    if (valueType != null && addTypeToGraph(valueType) && !valueType.isPrimitive())
                        methods.add(nodeManager.makeValue(valueName, typeName));
                } else if (memberDeclarationContext.fieldDeclaration() != null) {
                    String typeName = memberDeclarationContext.fieldDeclaration().typeName().getText();
                    String fieldName = memberDeclarationContext.fieldDeclaration().identifier().getText();
                    JavaTypeNode valueType = nodeManager.getTypeFromName(typeName);
                    if (valueType != null && addTypeToGraph(valueType) && !valueType.isPrimitive())
                        methods.add(nodeManager.makeField(fieldName, valueType, classNode));
                } else {
                    System.err.println("Warning: unknown class member " + memberDeclarationContext.getText());
                }
            }
        }
        return fnFlowGraph;
    }

    private boolean addTypeToGraph(JavaTypeNode type) {
        if (type.getName().contains("$"))
            return false;
        if (type.isPrimitive()) {
            fnFlowGraph.addVertex(type);
            return true;
        }
        String packageName = type.getPackageName();
        if(packageName.startsWith("sun.") || packageName.startsWith("com.sun."))
            return false;
        for (String okPackage : allowedPackages)
            if (packageName.startsWith(okPackage) || allowedPackages.size() == 1) {
                fnFlowGraph.addVertex(type);
                return true;
            }
        return false;
    }
}