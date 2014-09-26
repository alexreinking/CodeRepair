package com.intellij.codeInsight.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiStatement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class CodeRepairIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @NotNull @Override public String getText() {
        return "Repair single-statement type errors";
    }

    @NotNull @Override public String getFamilyName() {
        return getText();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement)
            throws IncorrectOperationException {
        PsiStatement highestLevel = getContainingStatement(psiElement);
        if(highestLevel != null) {
            System.err.println("------ " + highestLevel.getClass().getName() + " ------");
            System.err.println(highestLevel.getText());
            System.err.println("--------------" + highestLevel.getClass().getName().replaceAll(".", "-"));
            System.err.println("");
        }
    }

    @Override public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return getContainingStatement(psiElement) != null; // && (has a mistake)
    }

    private PsiStatement getContainingStatement(@NotNull PsiElement psiElement) {
        PsiElement current = psiElement.getOriginalElement();
        PsiStatement highest = null;
        while (current != null) {
            if (current instanceof PsiStatement)
                highest = (PsiStatement) current;
            current = current.getContext();
        }
        return highest;
    }
}
