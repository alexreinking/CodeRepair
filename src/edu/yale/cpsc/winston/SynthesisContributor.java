package edu.yale.cpsc.winston;

import coderepair.SynthesisGraph;
import coderepair.synthesis.CodeSnippet;
import coderepair.synthesis.CodeSynthesis;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.IconLoader;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

// Adapted from: https://github.com/JetBrains/intellij-community/blob/ff16ce78a1e0ddb6e67fd1dbc6e6a597e20d483a/java/compiler/impl/src/com/intellij/compiler/classFilesIndex/chainsSearch/completion/MethodsChainsCompletionContributor.java
public class SynthesisContributor extends CompletionContributor {
    private static final String graphFileName = "/Users/alexreinking/Development/CodeRepair/resources/graph.ser";
    private static final SynthesisGraph graph;

    static {
        SynthesisGraph theGraph = null;
        try {
            FileInputStream fileInput = new FileInputStream(graphFileName);
            ObjectInputStream in = new ObjectInputStream(fileInput);
            theGraph = (SynthesisGraph) in.readObject();
            in.close();
            fileInput.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        graph = theGraph;
    }


    private static final ElementPattern<PsiElement> ASSIGNMENT_PARENT = or(
            psiElement().withText(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED).afterSiblingSkipping(psiElement(PsiWhiteSpace.class),
                    psiElement(PsiJavaToken.class).withText("="))
    );

    private static final ElementPattern<? extends PsiElement> ASSIGNMENT_PATTERN =
            psiElement().withParent(ASSIGNMENT_PARENT).withSuperParent(2, or(
                            psiElement(PsiAssignmentExpression.class),
                            psiElement(PsiLocalVariable.class).inside(PsiDeclarationStatement.class))
            ).inside(PsiMethod.class);

    private static final ElementPattern<? extends PsiElement> METHOD_PARAM_PATTERN =
            psiElement().withSuperParent(3, PsiMethodCallExpressionImpl.class);

    public SynthesisContributor() {
        extend(CompletionType.SMART, ASSIGNMENT_PATTERN, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext processingContext,
                                          @NotNull CompletionResultSet completionResultSet) {
                if (graph == null) return;
                graph.resetLocals();
                CodeSynthesis synthesis = new CodeSynthesis(graph);

                SynthesisCompletionContext ctx = getTypeName(parameters);
                if (ctx != null) {
                    addLocalsToGraph(synthesis, ctx, parameters);
                    for (CodeSnippet codeSnippet : synthesis.synthesize(ctx.typeName, 5.0, 10))
                        completionResultSet.addElement(LookupElementBuilder.create(codeSnippet.code));
                }
            }
        });
    }

    private SynthesisCompletionContext getTypeName(CompletionParameters parameters) {
        final PsiElement parent = PsiTreeUtil.getParentOfType(parameters.getPosition(),
                PsiAssignmentExpression.class, PsiLocalVariable.class, PsiMethodCallExpression.class);
        if (parent == null) return null;

        String typeName = "";
        String variableName = "";
        PsiElement element = null;
        if (parent instanceof PsiLocalVariable) {
            typeName = ((PsiLocalVariable) parent).getType().getCanonicalText();
            variableName = ((PsiLocalVariable) parent).getName();
            element = PsiTreeUtil.getParentOfType(parent, PsiDeclarationStatement.class);
        } else if (parent instanceof PsiAssignmentExpression) {
            PsiAssignmentExpression assignment = (PsiAssignmentExpression) parent;
            PsiType type = assignment.getLExpression().getType();
            if (type == null) return null;
            typeName = type.getCanonicalText();
            final PsiIdentifier identifier = PsiTreeUtil.getChildOfType(assignment.getLExpression(), PsiIdentifier.class);
            if (identifier == null) return null;
            variableName = identifier.getText();
            element = assignment;
        } else if (parent instanceof PsiMethodCallExpression) {
            final PsiMethod method = ((PsiMethodCallExpression) parent).resolveMethod();
            if (method == null) return null;

            final PsiExpression expression = PsiTreeUtil.getParentOfType(parameters.getPosition(), PsiExpression.class);
            final PsiExpressionList expressionList = PsiTreeUtil.getParentOfType(parameters.getPosition(), PsiExpressionList.class);
            if (expressionList == null) return null;

            final int pos = Arrays.asList(expressionList.getExpressions()).indexOf(expression);
            final PsiParameter[] formals = method.getParameterList().getParameters();

            if (pos >= formals.length) return null;

            typeName = formals[pos].getType().getCanonicalText();
            element = PsiTreeUtil.getParentOfType(parent, PsiDeclarationStatement.class);
        }

        return new SynthesisCompletionContext(typeName, variableName, element);
    }

    private void addLocalsToGraph(CodeSynthesis synthesis,
                                  SynthesisCompletionContext context,
                                  CompletionParameters parameters) {
        final PsiElement position = parameters.getPosition();
        final PsiMethod containingMethod = PsiTreeUtil.getParentOfType(position, PsiMethod.class);
        if (containingMethod == null) return;
        final PsiClass aClass = containingMethod.getContainingClass();
        if (aClass == null) return;

        // Add in fields
        for (final PsiField field : aClass.getFields()) {
            final PsiClass containingClass = field.getContainingClass();
            if (containingClass != null) {
                boolean isPublic = field.hasModifierProperty(PsiModifier.PUBLIC);
                boolean isProtected = field.hasModifierProperty(PsiModifier.PROTECTED);
                boolean isPrivate = field.hasModifierProperty(PsiModifier.PRIVATE);
                boolean isPackageLocal = field.hasModifierProperty(PsiModifier.PACKAGE_LOCAL);
                String fieldName = field.getName();
                if ((isPublic || isProtected || (isPrivate || isPackageLocal) && aClass.isEquivalentTo(containingClass))
                        && !context.variableName.equals(fieldName)) {
                    graph.addLocalVariable(fieldName, field.getType().getCanonicalText(), 0.5);
                }
            }
        }

        final PsiCodeBlock body = containingMethod.getBody();
        if (body == null) return;
        for (PsiElement psiElement : body.getChildren()) {
            if (psiElement.isEquivalentTo(context.element))
                break;
            if (psiElement instanceof PsiDeclarationStatement) {
                for (PsiElement element : ((PsiDeclarationStatement) psiElement).getDeclaredElements()) {
                    if (element instanceof PsiLocalVariable) {
                        PsiLocalVariable localVariable = (PsiLocalVariable) element;
                        if (!context.variableName.equals(localVariable.getName()))
                            graph.addLocalVariable(localVariable.getName(), localVariable.getType().getCanonicalText());
                    }
                }

            }
        }
    }

    private static class SynthesisCompletionContext {
        public String typeName;
        public String variableName;
        public PsiElement element;

        public SynthesisCompletionContext(String typeName, String variableName, PsiElement element) {
            this.typeName = typeName;
            this.variableName = variableName;
            this.element = element;
        }
    }
}
