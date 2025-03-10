package com.linuxgods.kreiger.intellij.library.annotations;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.workspaceModel.core.fileIndex.WorkspaceFileIndex;
import com.intellij.workspaceModel.core.fileIndex.WorkspaceFileSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class LibraryTypeAnnotator implements Annotator {

    public static final TextAttributes ATTRIBUTES = new TextAttributes(null, null, null, null, Font.ITALIC);
    private static final Logger log = Logger.getInstance(LibraryTypeAnnotator.class);

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (!(psiElement instanceof PsiJavaCodeReferenceElement codeReferenceElement)) {
            return;
        }
        PsiElement resolved = codeReferenceElement.resolve();
        if (resolved == null) return;
        PsiFile containingPsiFile = psiElement.getContainingFile();
        PsiFile resolvedPsiFile = resolved.getContainingFile();
        if (containingPsiFile == null || resolvedPsiFile == null) return;
        VirtualFile containingFile = containingPsiFile.getVirtualFile();
        VirtualFile resolvedFile = resolvedPsiFile.getVirtualFile();
        if (containingFile == null || resolvedFile == null) {
            return;
        }
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(psiElement.getProject()).getFileIndex();
        LibraryOrderEntry libraryOrderEntry = getLibraryOrderEntry(fileIndex, resolvedFile);
        if (libraryOrderEntry == null) return;

        if (libraryOrderEntry.equals(getLibraryOrderEntry(fileIndex, containingFile))) {
            return;
        }

        annotationHolder.newAnnotation(HighlightSeverity.TEXT_ATTRIBUTES, "In "+libraryOrderEntry.getLibraryLevel()+" library "+libraryOrderEntry.getPresentableName())
                .enforcedTextAttributes(ATTRIBUTES)
                .create();
    }

    private static @Nullable LibraryOrderEntry getLibraryOrderEntry(ProjectFileIndex fileIndex, VirtualFile resolvedFile) {
        List<OrderEntry> orderEntries = fileIndex.getOrderEntriesForFile(resolvedFile);
        for (OrderEntry orderEntry : orderEntries) {
            if (orderEntry instanceof LibraryOrderEntry libraryOrderEntry) {
                return libraryOrderEntry;
            }
        }
        return null;
    }

}
