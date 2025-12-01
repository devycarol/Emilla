package net.emilla.command.app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.ActionMap;
import net.emilla.command.DataCommand;
import net.emilla.lang.Lines;
import net.emilla.util.Dialogs;
import net.emilla.util.Permission;
import net.emilla.util.Strings;
import net.emilla.util.TaskerIntent;

import java.util.Comparator;
import java.util.TreeSet;

/*internal*/ final class Tasker extends AppCommand implements DataCommand {

    public static final String PKG = TaskerIntent.TASKER_PACKAGE_MARKET;

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_app_tasker;
    }

    private static final String COL_TASK_NAME = "name";
    private static final String COL_PROJECT_NAME = "project_name";

    private enum Action {
        RUN, LIST
    }

    private final ActionMap<Action> mActionMap;

    /*internal*/ Tasker(Context ctx, AppEntry appEntry) {
        super(ctx, appEntry, EditorInfo.IME_ACTION_NEXT);

        var res = ctx.getResources();
        mActionMap = new ActionMap<Action>(res, Action.RUN, Action[]::new);

        mActionMap.put(res, Action.RUN, R.array.subcmd_tasker_run, true);
        mActionMap.put(res, Action.LIST, R.array.subcmd_tasker_list, false);
        // todo: list with search—when you do, change usesInstruction from false to true.
        // todo: in the far future, you could have a rudimentary UI for creating tasks
    }

    @Override
    protected void run(AssistActivity act, String task) {
        trySearchRun(act, extractAction(task), null);
    }

    @Override
    public void runWithData(AssistActivity act, String params) {
        trySearchRun(act, "", params);
    }

    @Override
    public void runWithData(AssistActivity act, String task, String params) {
        trySearchRun(act, extractAction(task), params);
    }

    private String extractAction(String task) {
        return Strings.emptyIfNull(mActionMap.get(task).instruction);
    }

    private void trySearchRun(AssistActivity act, String task, @Nullable String params) {
        switch (TaskerIntent.testStatus(act)) {
        case OK -> searchRun(act, task, params);
        case NOT_ENABLED -> failDialog(
            act, R.string.error_tasker_not_enabled,

            R.string.dlg_yes_tasker_open,
            (dlg, which) -> offerApp(act, this.appEntry.launchIntent(), true)
        );
        case NO_ACCESS -> failDialog(
            act, R.string.error_tasker_blocked,

            R.string.dlg_yes_tasker_external_access_settings,
            (dlg, which) -> offerApp(act, TaskerIntent.getExternalAccessPrefsIntent(), false)
        );
        case NO_PERMISSION -> Permission.TASKER.flow(
            act, () -> trySearchRun(act, task, params)
        );
        case NO_RECEIVER -> failMessage(act, R.string.error_tasker_no_receiver);
        }
    }

    private void searchRun(AssistActivity act, String task, @Nullable String params) {
        var res = act.getResources();
        var cr = act.getContentResolver();
        var contentUri = Uri.parse("content://net.dinglisch.android.tasker/tasks");
        String[] projection = {COL_TASK_NAME, COL_PROJECT_NAME};
        Cursor cur = cr.query(contentUri, projection, null, null, null);

        if (cur == null) {
            failMessage(act, res.getString(R.string.error_tasker_no_tasks, task));
            return;
        }

        int nameCol = 0;
        int projectCol = 1;

        String lcTask = task.toLowerCase();
        var tasks = new TreeSet<Task>(taskComparator(lcTask));

        while (cur.moveToNext()) {
            String taskName = cur.getString(nameCol);
            if (taskName.toLowerCase().contains(task.toLowerCase())) {
                String projectName = cur.getString(projectCol);
                tasks.add(new Task(projectName, taskName));
            }
        }

        cur.close();

        if (tasks.isEmpty()) {
            failMessage(act, res.getString(R.string.error_tasker_no_tasks, task));
            return;
        }

        int size = tasks.size();
        if (size == 1) {
            String taskName = tasks.first().taskName;
            if (taskName.equalsIgnoreCase(task)) {
                runTask(act, taskName, params);
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

        offerDialog(
            act,
            Dialogs.list(
                act, R.string.dialog_tasker_select_task,

                taskLabels,
                (dlg, which) -> runTask(act, taskNames[which], params)
            )
        );
        // todo: see if it's possible to display task/project icons
    }

    private static Comparator<Task> taskComparator(String lcTask) {
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

    private static void runTask(AssistActivity act, String taskName, @Nullable String params) {
        var intent = new TaskerIntent(taskName);
        if (params != null) {
            for (String param : new Lines(params, false)) {
                intent.addParameter(param);
            }
        }
        giveBroadcast(act, intent);
    }

}
