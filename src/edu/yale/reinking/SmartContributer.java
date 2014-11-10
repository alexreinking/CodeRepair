package edu.yale.reinking;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by ajr64 on 11/9/14.
 */
// Example: https://github.com/go-lang-plugin-org/go-lang-idea-plugin/blob/master/src/ro/redeul/google/go/lang/completion/GoCompletionContributor.java
public class SmartContributer extends CompletionContributor
{

    // PsiLocalVariable
    private static final ElementPattern<? extends PsiElement> LOCAL_VARAIBLE =
            psiElement(PsiExpression.class).afterLeaf("=").withParent(PsiLocalVariable.class);

    // PsiAssignmentExpression
    private static final ElementPattern<? extends PsiElement> ASSIGNMENT_EXPRESSION =
            psiElement(PsiExpression.class).afterLeaf("=").withParent(PsiAssignmentExpression.class);

    public SmartContributer() {
        extend(CompletionType.SMART, ASSIGNMENT_EXPRESSION, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

            }
        });
    }
}
