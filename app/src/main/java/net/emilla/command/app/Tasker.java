package net.emilla.command.app;

import android.database.Cursor;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.TaskerIntent;
import net.emilla.command.ActionMap;
import net.emilla.command.DataCommand;
import net.emilla.lang.Lines;
import net.emilla.util.Dialogs;
import net.emilla.util.Permissions;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Tasker extends AppCommand implements DataCommand {

    public static final String PKG = TaskerIntent.TASKER_PACKAGE_MARKET;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_tasker;
    @StringRes
    public static final int SUMMARY = R.string.summary_app_tasker;

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

    private enum Action {
        RUN, LIST
    }

    private ActionMap<Action> mActionMap;

    public Tasker(AssistActivity act, Yielder info) {
        super(act, new InstructyParams(info, R.string.instruction_app_tasker),
              R.string.summary_app_tasker,
              R.string.manual_app_tasker,
              EditorInfo.IME_ACTION_NEXT);
    }

    @Override @CallSuper
    protected void onInit() {
        super.onInit();

        if (mActionMap == null) {
            mActionMap = new ActionMap<>(Action.RUN);

            mActionMap.put(resources, Action.RUN, R.array.subcmd_tasker_run, true);
            mActionMap.put(resources, Action.LIST, R.array.subcmd_tasker_list, false);
            // todo: list with search—when you do, change usesInstruction from false to true.
            // todo: in the far future, you could have a rudimentary UI for creating tasks
        }
    }

    @Override @CallSuper
    protected void onClean() {
        super.onClean();
    }

    @Override
    protected void run(String task) {
        trySearchRun(extractAction(task), null);
    }

    @Override
    public void execute(String data) {
        String instruction = instruction();
        if (instruction == null) runWithData(data);
        else runWithData(instruction, data);
    }

    private void runWithData(String params) {
        trySearchRun("", params);
    }

    private void runWithData(String task, String params) {
        trySearchRun(extractAction(task), params);
    }

    private String extractAction(String task) {
        task = mActionMap.get(task).instruction();
        return task != null ? task : "";
    }

    private void trySearchRun(String task, @Nullable String params) {
        switch (TaskerIntent.testStatus(activity)) {
        case OK -> searchRun(task, params);
        case NOT_ENABLED -> failDialog(R.string.error_tasker_not_enabled,
                R.string.dlg_yes_tasker_open, (dlg, which) -> offerApp(app.launchIntent(), true));
        case NO_ACCESS -> failDialog(R.string.error_tasker_blocked,
                R.string.dlg_yes_tasker_external_access_settings,
                (dlg, which) -> offerApp(TaskerIntent.getExternalAccessPrefsIntent(), false));
        case NO_PERMISSION -> Permissions.taskerFlow(activity, () -> trySearchRun(task, params));
        case NO_RECEIVER -> failMessage(R.string.error_tasker_no_receiver);
        }
    }

    private void searchRun(String task, @Nullable String params) {
        String[] projection = {COL_TASK_NAME, COL_PROJECT_NAME};
        Cursor cur = contentResolver().query(CONTENT_URI, projection, null, null, null);

        if (cur == null) {
            failMessage(str(R.string.error_tasker_no_tasks, task));
            return;
        }

        int nameCol = 0, projectCol = 1;

        var lcTask = task.toLowerCase();
        SortedSet<Task> tasks = new TreeSet<>(taskComparator(lcTask));

        while (cur.moveToNext()) {
            var taskName = cur.getString(nameCol);
            if (taskName.toLowerCase().contains(task.toLowerCase())) {
                var projectName = cur.getString(projectCol);
                tasks.add(new Task(projectName, taskName));
            }
        }

        cur.close();

        if (tasks.isEmpty()) {
            failMessage(str(R.string.error_tasker_no_tasks, task));
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

        var taskNames = new String[size];
        var taskLabels = new String[size];
        int i = 0;
        for (Task tsk : tasks) {
            taskNames[i] = tsk.taskName;
            taskLabels[i] = tsk.toString();
            ++i;
        }

        offerDialog(Dialogs.list(activity, R.string.dialog_tasker_select_task, taskLabels,
                                 (dlg, which) -> runTask(taskNames[which], params)));
        // todo: see if it's possible to display task/project icons
    }

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

        @Override
        public String toString() {
            return taskName + " | " + projectName;
        }
    }

    private void runTask(String taskName, @Nullable String params) {
        var in = new TaskerIntent(taskName);
        if (params != null) {
            for (String param : new Lines(params, false)) in.addParameter(param);
        }
        giveBroadcast(in);
    }
}
