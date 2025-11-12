package net.emilla.file;

import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

import net.emilla.sort.SearchItem;

public final class TreeFile extends SearchItem {

    private final String mDocumentId;
    private final String mMimeType;

    public TreeFile(String documentId, String mimeType, String displayName) {
        super(displayName);

        mDocumentId = documentId;
        mMimeType = mimeType;
    }

    public Uri uri(Folder parent) {
        return DocumentsContract.buildDocumentUriUsingTree(parent.treeUri, mDocumentId);
    }

    public Intent viewIntent(Folder parent) {
        return Files.viewIntent(uri(parent), mMimeType);
    }

}
