package net.emilla.action.box;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.config.SettingVals;
import net.emilla.content.ResultLaunchers;
import net.emilla.databinding.FragmentNotesBinding;
import net.emilla.file.Folder;
import net.emilla.file.TreeFile;

public final class NotesFragment extends ActionBox {

    private final ActivityResultLauncher<Uri> mFolderSwitcher = registerForActivityResult(
        new OpenDocumentTree(),
        this::onFolderChosen
    );

    private NotesFragment() {
        super(R.layout.fragment_notes);
    }

    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    private /*late*/ FragmentNotesBinding mBinding;
    private /*late*/ FileSearchAdapter mAdapter;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        mBinding = FragmentNotesBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = mBinding.toolbar;
        toolbar.inflateMenu(R.menu.notes_toolbar);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.switch_folder) {
                switchFolder();
                return true;
            }
            return false;
        });

        var act = (AssistActivity) requireActivity();
        RecyclerView recycler = mBinding.recycler;

        var manager = new LinearLayoutManager(act);
        manager.setReverseLayout(true);
        recycler.setLayoutManager(manager);

        var inflater = act.getLayoutInflater();
        mAdapter = new FileSearchAdapter(
            inflater,
            treeFile -> act.setInstruction(R.string.command_note, treeFile.displayName)
        );
        recycler.setAdapter(mAdapter);

        TextView fileRequester = mBinding.fileRequester;
        fileRequester.setText(R.string.request_note_folder);
        fileRequester.setOnClickListener((View v) -> switchFolder());

        var cr = act.getContentResolver();
        SharedPreferences prefs = act.prefs();
        Uri noteFolder = SettingVals.noteFolder(prefs);
        if (noteFolder != null) {
            loadFolder(cr, prefs, noteFolder);
        }
    }

    private void switchFolder() {
        var act = (AssistActivity) requireActivity();

        Folder noteFolder = mAdapter.folder();
        Uri initialUri = noteFolder != null ? noteFolder.treeUri : null;
        ResultLaunchers.tryLaunch(act, mFolderSwitcher, initialUri);
    }

    private void onFolderChosen(@Nullable Uri folder) {
        if (folder == null) {
            return;
        }

        var act = (AssistActivity) requireActivity();
        var cr = act.getContentResolver();
        SharedPreferences prefs = act.prefs();

        if (loadFolder(cr, prefs, folder)) {
            int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            // todo: this should probably be checked for in the result contract. if these flags
            //  aren't given by the intent, the URI should be null to indicate failure. check the
            //  intent flags and try-catch a SecurityException in a custom contract. also this will
            //  almost never happen.
            cr.takePersistableUriPermission(folder, takeFlags);
            SettingVals.setNoteFolder(prefs, folder);
        }
    }

    private boolean loadFolder(ContentResolver cr, SharedPreferences prefs, Uri folder) {
        boolean loaded = loadedFolder(cr, folder);
        if (loaded) {
            mBinding.recycler.setVisibility(View.VISIBLE);
            mBinding.fileRequester.setVisibility(View.GONE);
        } else {
            SettingVals.forgetNoteFolder(prefs);

            mBinding.toolbar.setTitle(R.string.notes);

            mBinding.recycler.setVisibility(View.GONE);
            mBinding.fileRequester.setVisibility(View.VISIBLE);
        }
        return loaded;
    }

    private boolean loadedFolder(ContentResolver cr, Uri treeUri) {
        var folder = Folder.from(cr, treeUri);
        if (folder == null || !mAdapter.loadFolder(cr, folder)) {
            return false;
        }

        mBinding.toolbar.setTitle(folder.displayName);
        return true;
    }

    @Nullable
    public Folder folder() {
        return mAdapter.folder();
    }

    @Nullable
    public TreeFile fileNamed(String filename) {
        return mAdapter.exactItem(filename);
    }

    @Override
    public void instruct(@Nullable String instruction) {
        if (mAdapter != null) {
            // TODO BUG: does not search when "pre-instructed" i.e. move the cursor to the start of
            //  the command field and type the command
            mAdapter.search(instruction);
        }
    }

}
