package net.emilla.command.core;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.Environment.DIRECTORY_DOCUMENTS;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.core.content.FileProvider;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.settings.Aliases;
import net.emilla.app.Apps;
import net.emilla.util.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class Todo extends CoreDataCommand {

    public static final String ENTRY = "todo";
    @StringRes
    public static final int NAME = R.string.command_todo;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_todo;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Todo::new, ENTRY, NAME, ALIASES);
    }

    private final File mFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "todo.txt"); // TODO: allow configurable path and don't require all files permission
    private final Uri mUri = FileProvider.getUriForFile(activity, Apps.MY_PKG + ".fileprovider", mFile);
    private final Intent mViewIntent = new Intent(ACTION_VIEW).setDataAndType(mUri, "text/plain")
            .addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION)
            .putExtra(EXTRA_STREAM, mUri)
            .putExtra("EXTRA_FILEPATH", mFile.getAbsolutePath());

    public Todo(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_todo,
              R.drawable.ic_todo,
              R.string.summary_todo,
              R.string.manual_todo,
              R.string.data_hint_todo);
    }

    @Override
    protected void run() {
        appSucceed(mViewIntent);
    }

    @Override
    protected void run(String task) {
        // todo: newline handling unnecessary in this case
        todo(task); // TODO
        give(() -> toast(quantityString(R.plurals.toast_tasks_created, 1)));
    }

    @Override
    protected void runWithData(String tasks) {
        todo(tasks); // TODO

        int taskCount = 0;
        for (String t : tasks.split("\n")) {
            if (!t.isBlank()) ++taskCount;
        }
        toast(quantityString(R.plurals.toast_tasks_created, taskCount));
        give(() -> {});
    }

    @Override
    protected void runWithData(String task, String moreTasks) {
        todo(task + '\n' + moreTasks); // TODO

        int taskCount = 0;
        for (String t : task.split("\n")) {
            if (!t.isBlank()) ++taskCount;
        }
        toast(quantityString(R.plurals.toast_tasks_created, taskCount));
        give(() -> {});
    }

    private void todo(String task) { try {
        var cr = contentResolver();

        if (Files.endsWithNewline(cr, mUri)) task = "\n" + task + "\n";
        else task = task + "\n";

        ParcelFileDescriptor pfd = cr.openFileDescriptor(mUri, "wa");
        if (pfd == null) throw new FileNotFoundException();
        var fos = new FileOutputStream(pfd.getFileDescriptor());

        fos.write(task.getBytes());
        fos.close();
        pfd.close();
    } catch (FileNotFoundException e) {
        throw badCommand(R.string.error_cant_find_file);
    } catch (IOException e) {
        throw badCommand(R.string.error_cant_use_file);
    }}
}
