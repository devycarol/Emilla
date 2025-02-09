package net.emilla.command.app;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.DataCommand;
import net.emilla.lang.Lang;
import net.emilla.run.DialogFailure;
import net.emilla.run.MessageFailure;
import net.emilla.util.Dialogs;
import net.emilla.util.Permissions;
import net.emilla.util.app.TaskerIntent;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Tasker extends AppCommand implements DataCommand {

    public static final String PKG = TaskerIntent.TASKER_PACKAGE_MARKET;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_tasker;

    private static final class TaskerParams extends AppParams {

        private TaskerParams(Yielder info) {
            super(info,
                  EditorInfo.IME_ACTION_NEXT,
                  R.string.summary_app_tasker,
                  R.string.manual_app_tasker);
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, name, R.string.instruction_app_tasker);
        }
    }

    @Override
    public boolean usesData() {
        return true;
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_app_tasker;
    }

    private static final Uri CONTENT_URI = Uri.parse("content://net.dinglisch.android.tasker/tasks");

    private static final String COL_TASK_NAME = "name";
    private static final String COL_PROJECT_NAME = "project_name";

    public Tasker(AssistActivity act, Yielder info) {
        super(act, new TaskerParams(info));
    }

    @Override
    protected void run(@NonNull String task) {
        trySearchRun(extractAction(task), null);
    }

    @Override
    public void execute(@NonNull String data) {
        String instruction = instruction();
        if (instruction == null) runWithData(data);
        else runWithData(instruction, data);
    }

    private void runWithData(@NonNull String params) {
        trySearchRun("", params);
    }

    private void runWithData(@NonNull String task, @NonNull String params) {
        trySearchRun(extractAction(task), params);
    }

    private String extractAction(@NonNull String task) {
        if (task.startsWith("run")) task = task.substring(3).trim();
        else if (task.startsWith("list")) task = task.substring(4).trim();
        // TODO LANG: use TrieMap for all subcommands
        // todo: in the far future, you could have a rudimentary UI for creating tasks
        return task;
    }

    private void trySearchRun(@NonNull String task, @Nullable String params) {
        switch (TaskerIntent.testStatus(activity)) {
        case OK -> searchRun(task, params);
        case NOT_ENABLED -> fail(new DialogFailure(activity, Dialogs.dual(activity, R.string.error,
                R.string.error_tasker_not_enabled, R.string.dlg_yes_tasker_open, (dlg, which) -> {
            offerApp(launchIntent(), true);
            activity.onCloseDialog(false); // Todo: don't require this.
        })));
        case NO_ACCESS -> fail(new DialogFailure(activity, Dialogs.dual(activity, R.string.error,
                R.string.error_tasker_blocked, R.string.dlg_yes_tasker_external_access_settings, (dlg, which) -> {
            offerApp(TaskerIntent.getExternalAccessPrefsIntent(), false);
            activity.onCloseDialog(false); // Todo: don't require this.
        })));
        case NO_PERMISSION -> {
            if (Permissions.taskerFlow(activity, () -> trySearchRun(task, params))) {
                fail(new MessageFailure(activity, R.string.error, R.string.error_tasker_no_permission));
            }
        }
        case NO_RECEIVER -> fail(new MessageFailure(activity, R.string.error, R.string.error_tasker_no_receiver));
        }
    }

    private void searchRun(@NonNull String task, @Nullable String params) {
        ContentResolver cr = activity.getContentResolver();
        String[] projection = {COL_TASK_NAME, COL_PROJECT_NAME};
        Cursor cur = cr.query(CONTENT_URI, projection, null, null, null);

        if (cur == null) {
            String msg = string(R.string.error_tasker_no_tasks, task);
            fail(new MessageFailure(activity, R.string.error, msg));
            return;
        }

        int nameCol = 0, projectCol = 1;

        String lcTask = task.toLowerCase();
        SortedSet<Task> tasks = new TreeSet<>(taskComparator(lcTask));

        while (cur.moveToNext()) {
            String taskName = cur.getString(nameCol);
            if (taskName.toLowerCase().contains(task.toLowerCase())) {
                String projectName = cur.getString(projectCol);
                tasks.add(new Task(projectName, taskName));
            }
        }

        cur.close();

        if (tasks.isEmpty()) {
            String msg = string(R.string.error_tasker_no_tasks, task);
            fail(new MessageFailure(activity, R.string.error, msg));
            return;
        }

        int size = tasks.size();
        if (size == 1) {
            String taskName = tasks.first().taskName;
            if (taskName.equalsIgnoreCase(task)) {
                runTask(taskName, params);
                return;
            }
        }

        String[] taskNames = new String[size];
        String[] taskLabels = new String[size];
        int i = -1;
        for (Task tsk : tasks) {
            taskNames[++i] = tsk.taskName;
            taskLabels[i] = tsk.toString();
        }

        offerDialog(Dialogs.listBase(activity, R.string.dialog_tasker_select_task)
                .setItems(taskLabels, (dlg, which) -> {
            runTask(taskNames[which], params);
            activity.onCloseDialog(false); // Todo: don't require this.
        }));
        // todo: see if it's possible to display task/project icons
    }

    @NonNull
    private Comparator<Task> taskComparator(String lcTask) {
        return (a, b) -> {
            boolean aStarts = a.taskName.toLowerCase().startsWith(lcTask);
            boolean bStarts = b.taskName.toLowerCase().startsWith(lcTask);
            if (aStarts != bStarts) return aStarts ? -1 : 1;

            int projectComp = a.projectName.compareTo(b.projectName);
            if (projectComp != 0) return projectComp;

            return a.taskName.compareTo(b.taskName);
        };
    }

    private record Task(String projectName, String taskName) {

        @Override @NonNull
        public String toString() {
            return taskName + " | " + projectName;
        }
    }

    private void runTask(@NonNull String taskName, @Nullable String params) {
        TaskerIntent in = new TaskerIntent(taskName);
        if (params != null) {
            for (String param : params.split("\n")) if (!param.isBlank()) {
                // todo: Tasker allows multiline params.. could have an encoded line iterator
                //  similar to the CsvLine construct.
                in.addParameter(param);
            }
        }
        giveBroadcast(in);
    }
}
