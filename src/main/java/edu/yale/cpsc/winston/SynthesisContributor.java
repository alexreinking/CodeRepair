package edu.yale.cpsc.winston;

import coderepair.graph.SynthesisGraph;
import coderepair.synthesis.CodeSynthesis;
import coderepair.synthesis.ExpressionTreeBuilder;
import coderepair.synthesis.trees.ExpressionTree;
import coderepair.synthesis.valuations.AdditiveValuator;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.intellij.openapi.util.Key;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

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
            psiElement().withText(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED)
                    .afterSiblingSkipping(psiElement(PsiWhiteSpace.class),
                            psiElement(PsiJavaToken.class).withText("="))
    );

    private static final ElementPattern<? extends PsiElement> ASSIGNMENT_PATTERN =
            psiElement()
                    .withParent(ASSIGNMENT_PARENT)
                    .withSuperParent(2, or(
                                    psiElement(PsiAssignmentExpression.class),
                                    psiElement(PsiLocalVariable.class)
                                            .inside(PsiDeclarationStatement.class)
                            )
                    )
                    .inside(PsiMethod.class);

    private static final ElementPattern<? extends PsiElement> METHOD_PARAM_PATTERN =
            psiElement().withSuperParent(3, PsiMethodCallExpressionImpl.class);

    private static final Key<ExpressionTree> SNIPPET_ATTR = Key.create("snippet");

    public SynthesisContributor() {
        extend(CompletionType.SMART, ASSIGNMENT_PATTERN, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          ProcessingContext processingContext,
                                          @NotNull CompletionResultSet resultSet) {
                if (graph == null) return;
                graph.resetLocals();
                AdditiveValuator valuator = new AdditiveValuator(graph);
                CodeSynthesis synthesis = new CodeSynthesis(graph, new ExpressionTreeBuilder(valuator));

                SynthesisCompletionContext ctx = getTypeName(parameters);
                if (ctx != null) {
                    addLocalsToGraph(synthesis, ctx, parameters);

                    for (ExpressionTree codeSnippet : synthesis.synthesize(ctx.typeName, 0.8, 10)) {
                        LookupElementBuilder lookupElement = LookupElementBuilder.create(codeSnippet.asExpression());
                        lookupElement.putUserData(SNIPPET_ATTR, codeSnippet);
                        resultSet.addElement(lookupElement);
                    }

                    CompletionSorter ordering = CompletionSorter.defaultSorter(parameters, resultSet.getPrefixMatcher());
                    ordering.weigh(new LookupElementWeigher("WinstonScore") {
                        @Nullable
                        @Override
                        public Comparable weigh(@NotNull LookupElement element) {
                            return element.getUserData(SNIPPET_ATTR).getCost();
                        }
                    });

                    resultSet.withRelevanceSorter(ordering);
                }
            }
        });
    }

    private SynthesisCompletionContext getTypeName(CompletionParameters params) {
        final PsiElement parent = PsiTreeUtil.getParentOfType(params.getPosition(),
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
            final PsiIdentifier ident = PsiTreeUtil.getChildOfType(assignment.getLExpression(), PsiIdentifier.class);
            if (ident == null) return null;
            variableName = ident.getText();
            element = assignment;
        } else if (parent instanceof PsiMethodCallExpression) {
            final PsiMethod method = ((PsiMethodCallExpression) parent).resolveMethod();
            if (method == null) return null;

            final PsiExpression expression = PsiTreeUtil.getParentOfType(params.getPosition(), PsiExpression.class);
            final PsiExpressionList expList = PsiTreeUtil.getParentOfType(params.getPosition(), PsiExpressionList.class);
            if (expList == null) return null;

            final int pos = Arrays.asList(expList.getExpressions()).indexOf(expression);
            final PsiParameter[] formals = method.getParameterList().getParameters();

            if (pos >= formals.length) return null;

            typeName = formals[pos].getType().getCanonicalText();
            element = PsiTreeUtil.getParentOfType(parent, PsiDeclarationStatement.class);
        }

        return new SynthesisCompletionContext(typeName, variableName, element);
    }

    private void addLocalsToGraph(CodeSynthesis synthesis, // I'm conflicted about using this
                                  SynthesisCompletionContext ctx,
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
                        && !ctx.variableName.equals(fieldName)) {
                    graph.addLocalVariable(fieldName, field.getType().getCanonicalText(), 0.5);
                }
            }
        }

        // Go backwards and up to catch all the local variables in scope
        if (containingMethod.getBody() == null) return;
        double cost = 0.001;
        for (PsiElement cur = ctx.element;
             !cur.isEquivalentTo(containingMethod.getBody());
             cur = cur.getParent(), cost += 0.001)
            for (PsiElement sib = cur.getPrevSibling(); sib != null; sib = sib.getPrevSibling())
                if (sib instanceof PsiDeclarationStatement)
                    for (PsiElement element : ((PsiDeclarationStatement) sib).getDeclaredElements())
                        if (element instanceof PsiLocalVariable) {
                            PsiLocalVariable local = (PsiLocalVariable) element;
                            if (!ctx.variableName.equals(local.getName()))
                                graph.addLocalVariable(local.getName(), local.getType().getCanonicalText(), cost);
                        }
    }

    private static class SynthesisCompletionContext {
        public final String typeName;
        public final String variableName;
        public final PsiElement element;

        public SynthesisCompletionContext(String typeName, String variableName, PsiElement element) {
            this.typeName = typeName;
            this.variableName = variableName;
            this.element = element;
        }
    }
}
