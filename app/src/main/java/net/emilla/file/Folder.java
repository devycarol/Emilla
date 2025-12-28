package net.emilla.file;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;

import androidx.annotation.Nullable;

import net.emilla.cursor.Cursors;
import net.emilla.cursor.FileDisplayName;
import net.emilla.cursor.IsWritableDirectory;
import net.emilla.cursor.TextFiles;
import net.emilla.util.Intents;

public record Folder(Uri treeUri, Uri documentUri, String displayName) {

    @Nullable
    public static Folder from(ContentResolver cr, Uri treeUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && !DocumentsContract.isTreeUri(treeUri)) {
            return null;
        }

        Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(
            treeUri,
            DocumentsContract.getTreeDocumentId(treeUri)
        );
        if (!Cursors.testFirst(cr, documentUri, IsWritableDirectory.INSTANCE)) {
            return null;
        }

        String displayName = Cursors.extractFirst(cr, documentUri, FileDisplayName.INSTANCE);
        if (displayName == null) {
            return null;
        }

        return new Folder(treeUri, documentUri, displayName);
    }

    @Nullable
    public TreeFile[] textFiles(ContentResolver cr) {
        return Cursors.items(cr, childrenUri(), TextFiles.INSTANCE);
    }

    private Uri childrenUri() {
        return DocumentsContract.buildChildDocumentsUriUsingTree(
            this.treeUri,
            DocumentsContract.getTreeDocumentId(this.treeUri)
        );
    }

    public Intent viewIntent() {
        return Intents.view(documentUri, Document.MIME_TYPE_DIR);
    }

}
