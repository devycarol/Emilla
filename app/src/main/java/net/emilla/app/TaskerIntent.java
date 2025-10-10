// Version 1.3.3 (Emilla-modified)
// For usage examples see http://tasker.dinglisch.net/invoketasks.html

package net.emilla.app;

import static net.emilla.BuildConfig.DEBUG;
import static net.emilla.app.TaskerIntent.Status.NOT_ENABLED;
import static net.emilla.app.TaskerIntent.Status.NO_ACCESS;
import static net.emilla.app.TaskerIntent.Status.NO_PERMISSION;
import static net.emilla.app.TaskerIntent.Status.NO_RECEIVER;
import static net.emilla.app.TaskerIntent.Status.OK;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.os.Process;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TaskerIntent extends Intent {

    public static final String // 2 Tasker versions (Emilla only supports taskerm)
            TASKER_PACKAGE = "net.dinglisch.android.tasker",
            TASKER_PACKAGE_MARKET = TASKER_PACKAGE + "m";

    public static final String // Intent actions
            ACTION_TASK = TASKER_PACKAGE + ".ACTION_TASK",
            ACTION_TASK_COMPLETE = TASKER_PACKAGE + ".ACTION_TASK_COMPLETE",
            ACTION_TASK_SELECT = TASKER_PACKAGE + ".ACTION_TASK_SELECT";

    public static final String // Intent parameters
            EXTRA_ACTION_INDEX_PREFIX = "action",
            TASK_NAME_DATA_SCHEME = "task",
            EXTRA_TASK_NAME = "task_name",
            EXTRA_TASK_PRIORITY = "task_priority",
            EXTRA_SUCCESS_FLAG = "success",
            EXTRA_VAR_NAMES_LIST = "varNames",
            EXTRA_VAR_VALUES_LIST = "varValues",
            EXTRA_TASK_OUTPUT = "output";

    public static final String // Content provider columns
            PROVIDER_COL_NAME_EXTERNAL_ACCESS = "ext_access",
            PROVIDER_COL_NAME_ENABLED = "enabled";

    @Deprecated // use EXTRA_VAR_NAMES_LIST, EXTRA_VAR_VALUES_LIST
    public static final String EXTRA_PARAM_LIST = "params";

    // Intent data

    public static final String TASK_ID_SCHEME = "id";

    // For particular actions

    public static final String DEFAULT_ENCRYPTION_KEY = "default";
    public static final String ENCRYPTED_AFFIX = "tec";
    public static final int MAX_ARGS = 10;

    // Bundle keys—only useful for Tasker
    public static final String
            ACTION_CODE = "action",
            APP_ARG_PREFIX = "app:",
            ICON_ARG_PREFIX = "icn:",
            ARG_INDEX_PREFIX = "arg:",
            PARAM_VAR_NAME_PREFIX = "par";

    // Misc

    public static final String PERMISSION_RUN_TASKS = TASKER_PACKAGE + ".PERMISSION_RUN_TASKS";

    public static final String  ACTION_OPEN_PREFS = TASKER_PACKAGE + ".ACTION_OPEN_PREFS";
    public static final String  EXTRA_OPEN_PREFS_TAB_NO = "tno";
    private static final int    MISC_PREFS_TAB_NO = 3;  // 0 based

    private static final String // To query whether Tasker is enabled and external access is enabled
            TASKER_PREFS_URI = "content://" + TASKER_PACKAGE + "/prefs";


    /// result values for testStatus
    public enum Status {
        /// Tasker is not enabled.
        NOT_ENABLED,
        /// User prefs disallow external access.
        NO_ACCESS,
        /// Calling app does not have permission PERMISSION_RUN_TASKS.
        NO_PERMISSION,
        /// Tasker has not created a listener for external access (probably a Tasker bug).
        NO_RECEIVER,
        /// You should be able to send a task to run. Still need to listen for result for e.g. task
        /// not found.
        OK
    }

    // -------------------------- PRIVATE VARS ---------------------------- //

    private static final String TAG = "TaskerIntent";

    private static final String
            EXTRA_INTENT_VERSION_NUMBER = "version_number",
            INTENT_VERSION_NUMBER = "1.1";

    public static final int // Inclusive values
            MIN_PRIORITY = 0,
            MAX_PRIORITY = 10;

    // Tracking state
    private int mActionCount = 0;
    private int mArgCount;

    // -------------------------- PUBLIC METHODS ---------------------------- //

    public static boolean validatePriority(int pri) {
        return MIN_PRIORITY <= pri && pri <= MAX_PRIORITY;
    }

    // test we can send a TaskerIntent to Tasker
    // use *before* sending an intent
    // still need to test the *result after* sending intent

    public static Status testStatus(Context ctx) {
        if (prefNotSet(ctx, PROVIDER_COL_NAME_ENABLED)) return NOT_ENABLED;
        if (prefNotSet(ctx, PROVIDER_COL_NAME_EXTERNAL_ACCESS)) return NO_ACCESS;
        if (!hasPermission(ctx)) return NO_PERMISSION;
        if (!new TaskerIntent("").receiverExists(ctx)) return NO_RECEIVER;
        return OK;
    }

    // Check if Tasker installed

    public static IntentFilter getCompletionFilter(String taskName) {
        var filter = new IntentFilter(ACTION_TASK_COMPLETE);

        filter.addDataScheme(TASK_NAME_DATA_SCHEME);
        filter.addDataPath(taskName, PatternMatcher.PATTERN_LITERAL);

        return filter;
    }

    public static Intent selectTask() {
        int flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION
                  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                  | Intent.FLAG_ACTIVITY_NO_HISTORY;
        return new Intent(ACTION_TASK_SELECT).setFlags(flags);
    }

    // public access deprecated, use testSend() instead

    public static boolean hasPermission(Context ctx) {
        int result = ctx.checkPermission(PERMISSION_RUN_TASKS, Process.myPid(), Process.myUid());
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // Get an intent that will bring up the Tasker prefs screen with the External Access control(s)
    // Probably you want to use startActivity or startActivityForResult with it

    public static Intent getExternalAccessPrefsIntent() {
        return new Intent(ACTION_OPEN_PREFS).putExtra(EXTRA_OPEN_PREFS_TAB_NO, MISC_PREFS_TAB_NO);
    }

    // ------------------------------------- INSTANCE METHODS ----------------------------- //

    public TaskerIntent() {
        super(ACTION_TASK);
        setRandomData();
        putMetaExtras(getRandomString());
    }

    public TaskerIntent(String taskName) {
        super(ACTION_TASK);
        setRandomData();
        putMetaExtras(taskName);
    }

    public TaskerIntent setTaskPriority(int priority) {
        if (validatePriority(priority)) putExtra(EXTRA_TASK_PRIORITY, priority);
        else if (DEBUG) Log.e(TAG, "priority out of range: " + MIN_PRIORITY + ":" + MAX_PRIORITY);

        return this;
    }

    // Sets subsequently %par1, %par2 etc
    public TaskerIntent addParameter(String value) {
        int index = 1;

        Bundle extras = getExtras();
        if (extras != null && extras.containsKey(EXTRA_VAR_NAMES_LIST)) {
            ArrayList<String> varNames = extras.getStringArrayList(EXTRA_VAR_NAMES_LIST);
            if (varNames != null) index = varNames.size() + 1;
        }

        if (DEBUG) Log.d(TAG, "index: " + index);

        addLocalVariable("%" + PARAM_VAR_NAME_PREFIX + index, value);

        return this;
    }

    // Arbitrary specification of (local) variable names and values
    public TaskerIntent addLocalVariable(String name, String value) {
        ArrayList<String> names = getStringArrayListExtra(EXTRA_VAR_NAMES_LIST);
        if (names == null) {
            names = new ArrayList<String>();
            putStringArrayListExtra(EXTRA_VAR_NAMES_LIST, names);
        }

        ArrayList<String> values = getStringArrayListExtra(EXTRA_VAR_VALUES_LIST);
        if (values == null) {
            values = new ArrayList<String>();
            putStringArrayListExtra(EXTRA_VAR_VALUES_LIST, values);
        }

        names.add(name);
        values.add(value);

        return this;
    }

    public TaskerIntent addAction(int code) {
        mActionCount++;
        mArgCount = 1;

        var actionBundle = new Bundle();

        actionBundle.putInt(ACTION_CODE, code);

        // Add action bundle to intent
        putExtra(EXTRA_ACTION_INDEX_PREFIX + mActionCount, actionBundle);

        return this;
    }

    // string arg
    public TaskerIntent addArg(String arg) {
        Bundle b = getActionBundle();

        if (b != null) {
            b.putString(ARG_INDEX_PREFIX + mArgCount, arg);
            mArgCount++;
        }

        return this;
    }

    // int arg
    public TaskerIntent addArg(int arg) {
        Bundle b = getActionBundle();

        if (b != null) {
            b.putInt(ARG_INDEX_PREFIX + mArgCount, arg);
            mArgCount++;
        }

        return this;
    }

    // boolean arg
    public TaskerIntent addArg(boolean arg) {
        Bundle b = getActionBundle();

        if (b != null) {
            b.putBoolean(ARG_INDEX_PREFIX + mArgCount, arg);
            mArgCount++;
        }

        return this;
    }

    // Application arg
    public TaskerIntent addArg(String pkg, String cls) {
        Bundle b = getActionBundle();

        if (b != null) {
            String builder = APP_ARG_PREFIX + pkg + "," + cls;
            b.putString(ARG_INDEX_PREFIX + mArgCount, builder);
            mArgCount++;
        }

        return this;
    }

    public IntentFilter getCompletionFilter() {
        return getCompletionFilter(getTaskName());
    }

    public String getTaskName() {
        return getStringExtra(EXTRA_TASK_NAME);
    }

    public boolean receiverExists(Context ctx) {
        List<ResolveInfo> recs = ctx.getPackageManager().queryBroadcastReceivers(this, 0);
        return !recs.isEmpty();
    }

    // -------------------- PRIVATE METHODS -------------------- //

    private static String getRandomString() {
        return Long.toString(new Random().nextLong());
    }

    // so that if multiple TaskerIntents are used in PendingIntents there's virtually no
    // clash chance
    private void setRandomData() {
        setData(Uri.parse(TASK_ID_SCHEME + ":" + getRandomString()));
    }

    @Nullable
    private Bundle getActionBundle() {
        Bundle actionBundle = null;

        if (mArgCount > MAX_ARGS) {
            if (DEBUG) Log.e(TAG, "maximum number of arguments exceeded (" + MAX_ARGS + ")");
        } else {
            String key = EXTRA_ACTION_INDEX_PREFIX + mActionCount;

            if (this.hasExtra(key)) actionBundle = getBundleExtra(key);
            else if (DEBUG) Log.e(TAG, "no actions added yet");
        }

        return actionBundle;
    }

    private void putMetaExtras(String taskName) {
        putExtra(EXTRA_INTENT_VERSION_NUMBER, INTENT_VERSION_NUMBER);
        putExtra(EXTRA_TASK_NAME, taskName);
    }

    // for testing that Tasker is enabled and external access is allowed

    private static boolean prefNotSet(Context ctx, String col) {
        String[] proj = {col};

        Cursor c = ctx.getContentResolver().query(Uri.parse(TASKER_PREFS_URI), proj, null, null, null);

        boolean notAccepting = true;

        if (c == null) {
            if (DEBUG) Log.w(TAG, "no cursor for " + TASKER_PREFS_URI);
        } else {
            c.moveToFirst();

            if (Boolean.TRUE.toString().equals(c.getString(0))) notAccepting = false;

            c.close();
        }

        return notAccepting;
    }
}