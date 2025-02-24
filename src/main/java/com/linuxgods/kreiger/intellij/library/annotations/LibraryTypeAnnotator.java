package com.linuxgods.kreiger.intellij.library.annotations;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LibraryTypeAnnotator implements Annotator {

    public static final TextAttributes ATTRIBUTES = new TextAttributes(null, null, null, null, Font.ITALIC);

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof PsiJavaCodeReferenceElement codeReferenceElement) {
            annotateClassNameElement(psiElement, annotationHolder, codeReferenceElement.resolve());
        }
    }

    private static void annotateClassNameElement(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder, PsiElement resolved) {
        if (resolved == null) return;
        PsiFile containingFile = resolved.getContainingFile();
        if (containingFile == null) return;
        Project project = psiElement.getProject();
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        VirtualFile virtualFile = containingFile.getVirtualFile();
        if (virtualFile == null || !projectFileIndex.isInLibrary(virtualFile)) {
            return;
        }
        annotationHolder.newAnnotation(HighlightSeverity.TEXT_ATTRIBUTES, "Library class")
                .enforcedTextAttributes(ATTRIBUTES)
                .create();
    }
}
