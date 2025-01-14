package net.emilla.command.core;

import static android.content.Intent.*;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static androidx.core.content.FileProvider.getUriForFile;
import static java.util.Objects.requireNonNull;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Todo extends CoreDataCommand {

    public static final String ENTRY = "todo";
    @StringRes
    public static final int NAME = R.string.command_todo;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_todo;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Todo::new, ENTRY, NAME, ALIASES);
    }

    private static class TodoParams extends CoreDataParams {

        private TodoParams() {
            super(NAME,
                  R.string.instruction_todo,
                  R.drawable.ic_todo,
                  R.string.summary_todo,
                  R.string.manual_todo,
                  R.string.data_hint_todo);
        }
    }

    private final File mFile = new File(getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "todo.txt"); // TODO: allow configurable path and don't require all files permission
    private final Uri mUri = getUriForFile(activity, Apps.MY_PKG + ".fileprovider", mFile);
    private final Intent mViewIntent = new Intent(ACTION_VIEW).setDataAndType(mUri, "text/plain")
            .addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION)
            .putExtra(EXTRA_STREAM, mUri)
            .putExtra("EXTRA_FILEPATH", mFile.getAbsolutePath());

    public Todo(AssistActivity act) {
        super(act, new TodoParams());
    }

    private void todo(String task) /* todo FRICK + filenotfound */ {
        ContentResolver cr = activity.getContentResolver();
    try {
        InputStream is = requireNonNull(cr.openInputStream(mUri)); // todo null safety ???
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        int c, lastChar = '\n';
        while ((c = reader.read()) != -1) lastChar = c;
        reader.close();
        is.close();

        ParcelFileDescriptor pfd = requireNonNull(cr.openFileDescriptor(mUri, "wa")); // todo null safety ???
        FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
        fos.write(((lastChar == '\n' ? "" : '\n') + task + '\n').getBytes());
        fos.close();
        pfd.close();
    } catch (IOException e) {
        throw new EmlaBadCommandException(NAME, R.string.error_lmao); // TODO LMAO
    }}

    @Override
    protected void run() {
        appSucceed(mViewIntent);
    }

    @Override
    protected void run(String task) {
        // todo: newline handling unnecessary in this case
        todo(task); // TODO
        giveText(quantityString(R.plurals.toast_tasks_created, 1), false);
    }

    @Override
    protected void runWithData(String tasks) {
        todo(tasks); // TODO

        int taskCount = 0;
        for (String t : tasks.split("\n")) if (!t.isBlank()) ++taskCount;
        giveText(quantityString(R.plurals.toast_tasks_created, taskCount), false);
    }

    @Override
    protected void runWithData(String task, String moreTasks) {
        todo(task + '\n' + moreTasks); // TODO

        int taskCount = 0;
        for (String t : task.split("\n")) if (!t.isBlank()) ++taskCount;
        giveText(quantityString(R.plurals.toast_tasks_created, taskCount), false);
    }
}
