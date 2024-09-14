package net.emilla.commands;

import static android.content.Intent.*;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static androidx.core.content.FileProvider.getUriForFile;
import static java.util.Objects.requireNonNull;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;
import net.emilla.utils.Apps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandTodo extends CoreDataCommand {
private final File mFile = new File(getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "todo.txt"); // TODO: allow configurable path and don't require all files permission
private final Uri mUri = getUriForFile(activity(), packageName() + ".fileprovider", mFile);
private final Intent mViewIntent = Apps.newTask(ACTION_VIEW, mUri, "text/plain")
        .addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION)
        .putExtra(EXTRA_STREAM, mUri)
        .putExtra("EXTRA_FILEPATH", mFile.getAbsolutePath());

public CommandTodo(final AssistActivity act, final String instruct) {
    super(act, instruct, R.string.command_todo, R.string.instruction_todo);
}

@Override @StringRes
public int dataHint() {
    return R.string.data_hint_todo;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_todo;
}

private void todo(final String task) /* todo FRICK + filenotfound */ {
    final ContentResolver cr = activity().getContentResolver();
try {
    final InputStream is = requireNonNull(cr.openInputStream(mUri)); // todo null safety ???
    final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    int c, lastChar = '\n';
    while ((c = reader.read()) != -1) lastChar = c;
    reader.close();
    is.close();

    final ParcelFileDescriptor pfd = requireNonNull(cr.openFileDescriptor(mUri, "wa")); // todo null safety ???
    final FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
    fos.write(((lastChar == '\n' ? "" : '\n') + task + '\n').getBytes());
    fos.close();
    pfd.close();
} catch (IOException e) {
    throw new EmlaBadCommandException("This is really really bad!"); // TODO LMAO
}}

@Override
protected void run() {
    if (mViewIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No app found to view text files.");
    succeed(mViewIntent);
}

@Override
protected void run(final String task) {
    // todo: newline handling unnecessary in this case
    todo(task); // TODO
    give(string(R.string.toast_task_created), false); // TODO: lang
}

@Override
protected void runWithData(final String tasks) {
    todo(tasks); // TODO

    int taskCount = 0;
    for (final String t : tasks.split("\n"))
    if (!t.isBlank()) ++taskCount;
    final String msg = taskCount == 1 ? string(R.string.toast_task_created)
            : string(R.string.toast_tasks_created, taskCount); // TODO: look into getQuantityString
    give(msg, false);
}

@Override
protected void runWithData(final String task, final String moreTasks) {
    todo(task + '\n' + moreTasks); // TODO

    int taskCount = 0;
    for (final String t : task.split("\n"))
    if (!t.isBlank()) ++taskCount;
    final String msg = taskCount == 1 ? string(R.string.toast_task_created)
            : string(R.string.toast_tasks_created, taskCount); // TODO: look into getQuantityString
    give(msg, false);
}
}
