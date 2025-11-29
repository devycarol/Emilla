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
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.config.SettingVals;
import net.emilla.content.ResultLaunchers;
import net.emilla.cursor.Cursors;
import net.emilla.cursor.FileDisplayName;
import net.emilla.databinding.FragmentNotesBinding;
import net.emilla.util.MimeTypes;

public final class ListFileFragment extends ActionBox {

    private final ActivityResultLauncher<String[]> mFileSwitcher = registerForActivityResult(
        new OpenDocument(),
        this::onFileChosen
    );

    private ListFileFragment() {
        super(R.layout.fragment_notes);
    }

    public static ListFileFragment newInstance() {
        return new ListFileFragment();
    }

    private /*late*/ FragmentNotesBinding mBinding;
    private /*late*/ ListFileAdapter mAdapter;

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
        toolbar.inflateMenu(R.menu.list_file_toolbar);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.switch_file) {
                switchFile();
                return true;
            }
            return false;
        });

        var act = (AssistActivity) requireActivity();
        RecyclerView recycler = mBinding.recycler;

        var manager = new LinearLayoutManager(act);
        recycler.setLayoutManager(manager);

        var inflater = act.getLayoutInflater();
        mAdapter = new ListFileAdapter(inflater);
        recycler.setAdapter(mAdapter);

        TextView fileRequester = mBinding.fileRequester;
        fileRequester.setText(R.string.request_todo_file);
        fileRequester.setOnClickListener((View v) -> switchFile());

        var cr = act.getContentResolver();
        SharedPreferences prefs = act.prefs();
        Uri todoFile = SettingVals.todoFile(prefs);
        if (todoFile != null) {
            loadFile(cr, prefs, todoFile);
        }
    }

    private void switchFile() {
        var act = (AssistActivity) requireActivity();

        String[] mimeTypes = { MimeTypes.ANY_TEXT };
        ResultLaunchers.tryLaunch(act, mFileSwitcher, mimeTypes);
    }

    private void onFileChosen(@Nullable Uri file) {
        if (file == null) {
            return;
        }

        var act = (AssistActivity) requireActivity();
        var cr = act.getContentResolver();
        SharedPreferences prefs = act.prefs();

        if (loadFile(cr, prefs, file)) {
            int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            // todo: this should probably be checked for in the result contract. if these flags
            //  aren't given by the intent, the URI should be null to indicate failure. check the
            //  intent flags and try-catch a SecurityException in a custom contract. also this will
            //  almost never happen.
            cr.takePersistableUriPermission(file, takeFlags);
            SettingVals.setTodoFile(prefs, file);
        }
    }

    private boolean loadFile(ContentResolver cr, SharedPreferences prefs, Uri file) {
        boolean loaded = loadedFile(cr, file);
        if (loaded) {
            mBinding.recycler.setVisibility(View.VISIBLE);
            mBinding.fileRequester.setVisibility(View.GONE);
        } else {
            SettingVals.forgetTodoFile(prefs);

            mBinding.toolbar.setTitle(R.string.command_todo);

            mBinding.recycler.setVisibility(View.GONE);
            mBinding.fileRequester.setVisibility(View.VISIBLE);
        }
        return loaded;
    }

    private boolean loadedFile(ContentResolver cr, Uri file) {
        String filename = Cursors.extractFirst(cr, file, FileDisplayName.INSTANCE);
        if (filename == null || !mAdapter.loadFile(cr, file)) {
            return false;
        }

        mBinding.toolbar.setTitle(filename);
        return true;
    }

    @Nullable
    public Uri file() {
        return mAdapter.file();
    }

    public TriResult addTask(String task) {
        var ctx = requireContext();
        var cr = ctx.getContentResolver();
        return mAdapter.addItem(cr, task);
    }

    @Override
    public void instruct(@Nullable String instruction) {
        // do nothing
    }

}
