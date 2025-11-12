package net.emilla.action.box;

import android.content.ContentResolver;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import net.emilla.file.Folder;
import net.emilla.file.TreeFile;
import net.emilla.sort.ItemSearchAdapter;

import java.util.function.Consumer;

public final class FileSearchAdapter extends ItemSearchAdapter<TreeFile> {

    private static final TreeFile[] EMPTY_ARRAY = new TreeFile[0];

    @Nullable
    private Folder mFolder;

    public FileSearchAdapter(LayoutInflater inflater, Consumer<TreeFile> itemClickAction) {
        super(inflater, EMPTY_ARRAY, itemClickAction, TreeFile[]::new);
    }

    public boolean loadFolder(ContentResolver cr, Folder folder) {
        TreeFile[] textFiles = folder.textFiles(cr);
        if (textFiles == null) {
            return false;
        }

        this.searcher.load(textFiles);
        mFolder = folder;

        refresh();

        return true;
    }

    @Nullable
    public Folder folder() {
        return mFolder;
    }

}
