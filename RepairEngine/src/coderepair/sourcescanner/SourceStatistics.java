package coderepair.sourcescanner;

import coderepair.SynthesisGraph;
import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceStatistics implements Plugin {

    private final SynthesisGraph graph;

    public SourceStatistics() {
        try {
            System.out.println("Loading SourceStatistics plugin.");

            FileInputStream fileInput = new FileInputStream("/home/alex/Development/CodeRepair/data/graph.ser");
            ObjectInputStream in = new ObjectInputStream(fileInput);
            graph = (SynthesisGraph) in.readObject();
            in.close();
            fileInput.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "coderepair.sourcescanner.SourceStatistics";
    }

    @Override
    public void init(JavacTask javacTask, String... strings) {
        javacTask.addTaskListener(new StatisticsListener(javacTask));
    }

    private class MethodCounter extends TreePathScanner<Object, Void> {

        private final Types types;
        private final Trees trees;

        public MethodCounter(JavacTask task) {
            types = task.getTypes();
            trees = Trees.instance(task);
        }

        @Override
        public Object visitMethodInvocation(MethodInvocationTree methodInvocationTree, Void aVoid) {
            ExpressionTree methodSelect = methodInvocationTree.getMethodSelect();
            TypeMirror outputTypeMirror = trees.getTypeMirror(new TreePath(getCurrentPath(), methodInvocationTree));
            String outputType = (outputTypeMirror == null) ? "" : outputTypeMirror.toString();
            if (outputType.startsWith("<anonymous "))
                outputType = outputType.substring(11, outputType.length() - 1);
            String functionName = "";
            List<String> argumentTypes = new ArrayList<>();
            for (ExpressionTree expressionTree : methodInvocationTree.getArguments()) {
                TypeMirror typeMirror = trees.getTypeMirror(new TreePath(getCurrentPath(), expressionTree));
                if (typeMirror == null) {
                    System.err.printf("No type found for %s\n", expressionTree.toString());
                    return super.visitMethodInvocation(methodInvocationTree, aVoid);
                }
                argumentTypes.add(typeMirror.toString());
            }

            if (methodSelect.getKind() == Tree.Kind.MEMBER_SELECT) {
                MemberSelectTree memberSelectTree = (MemberSelectTree) methodInvocationTree.getMethodSelect();
                TypeMirror typeMirror = trees.getTypeMirror(new TreePath(getCurrentPath(), memberSelectTree.getExpression()));

                if (typeMirror == null) {
                    System.err.printf("No type found for %s\n", memberSelectTree.getExpression().toString());
                    return super.visitMethodInvocation(methodInvocationTree, aVoid);
                }

                String ownerType = typeMirror.toString();
                argumentTypes.add(0, ownerType);
                functionName = ownerType + "." + memberSelectTree.getIdentifier().toString();
            } else {
                if (methodSelect.getKind() == Tree.Kind.IDENTIFIER) {
                    Name identifierTree = ((IdentifierTree) methodInvocationTree.getMethodSelect()).getName();
                    if (!identifierTree.contentEquals("super"))
                        functionName = identifierTree.toString();
                }
            }
            graph.lookupFunction(functionName, outputType, argumentTypes);
            return super.visitMethodInvocation(methodInvocationTree, aVoid);
        }

        @Override
        public Object visitNewClass(NewClassTree newClassTree, Void aVoid) {
            TypeMirror typeMirror = trees.getTypeMirror(new TreePath(getCurrentPath(), newClassTree));
            if (typeMirror == null) {
                System.err.printf("No type found for %s\n", newClassTree.toString());
                return super.visitNewClass(newClassTree, aVoid);
            }
            String className = typeMirror.toString();
            if (className.startsWith("<anonymous "))
                className = className.substring(11, className.length() - 1);
            List<String> argumentTypes = newClassTree.getArguments().stream().map(expressionTree -> trees.getTypeMirror(new TreePath(getCurrentPath(), expressionTree)).toString()).collect(Collectors.toList());
            graph.lookupFunction("new " + className, className, argumentTypes);
            return super.visitNewClass(newClassTree, aVoid);
        }
    }

    private class StatisticsListener implements TaskListener {

        private final MethodCounter visitor;

        public StatisticsListener(JavacTask task) {
            visitor = new MethodCounter(task);
        }

        @Override
        public void started(TaskEvent taskEvent) {
        }

        @Override
        public void finished(TaskEvent taskEvent) {
            if (taskEvent.getKind() == TaskEvent.Kind.ANALYZE) {
                CompilationUnitTree compilationUnit = taskEvent.getCompilationUnit();
                visitor.scan(compilationUnit, null);
            }
        }
    }
}
