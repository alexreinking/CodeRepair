package coderepair.sourcescanner;

import coderepair.SynthesisGraph;
import coderepair.analysis.JavaFunctionNode;
import coderepair.analysis.JavaGraphNode;
import coderepair.analysis.JavaTypeNode;
import com.sun.source.tree.*;
import com.sun.source.util.*;
import org.jgrapht.Graphs;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
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
            List<String> argumentTypes = new ArrayList<String>();
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
            matchFunction(functionName, outputType, argumentTypes);
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
            List<String> argumentTypes = new ArrayList<String>();
            for (ExpressionTree expressionTree : newClassTree.getArguments())
                argumentTypes.add(trees.getTypeMirror(new TreePath(getCurrentPath(), expressionTree)).toString());
            matchFunction("new " + className, className, argumentTypes);
            return super.visitNewClass(newClassTree, aVoid);
        }

        private JavaFunctionNode matchFunction(String functionName, String outputType, List<String> argumentTypes) {
            if (functionName.isEmpty()) return null;
            String formalName = String.format("%s: (%s) -> %s",
                    functionName, String.join(" x ", argumentTypes), outputType);

            JavaFunctionNode functionNode = null;

            try {
                JavaGraphNode outputVertex = graph.getVertexByName(outputType);

                for (JavaGraphNode generatorNode : Graphs.successorListOf(graph, outputVertex))
                    if (generatorNode instanceof JavaFunctionNode) {
                        JavaFunctionNode currentCandidate = (JavaFunctionNode) generatorNode;
                        if (functionName.equals(currentCandidate.getFunctionName())) {
                            List<String> actualTypes = new ArrayList<String>();
                            for (JavaTypeNode javaTypeNode : currentCandidate.getSignature())
                                actualTypes.add(javaTypeNode.getName());

                            if (formalName.equals(currentCandidate.getName())) {
                                functionNode = currentCandidate;
                                break;
                            } else if (argumentsMatch(actualTypes, argumentTypes)) {
                                functionNode = currentCandidate;
                            }
                        }
                    }

//                if (functionNode != null)
//                    System.out.printf("       Found:\t%s\n", functionNode.getName());
//                else
//                    System.err.printf("     Missing:\t%s\n", formalName);
            } catch (IllegalArgumentException e) {
                System.err.printf("No output type %s found in graph.%n", outputType);
            }
            return functionNode;
        }

        private boolean argumentsMatch(List<String> actualTypes, List<String> givenTypes) {
            if (actualTypes.size() != givenTypes.size())
                return false;

            try {
                Iterator<String> actualIt = actualTypes.iterator();
                Iterator<String> givenIt = givenTypes.iterator();
                while (actualIt.hasNext() && givenIt.hasNext())
                    if (!Class.forName(actualIt.next()).isAssignableFrom(Class.forName(givenIt.next())))
                        return false;
                return true;
            } catch (Exception e) {
                return false;
            }
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
