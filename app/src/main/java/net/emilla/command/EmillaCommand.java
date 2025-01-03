package net.emilla.command;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.QuickAction;
import net.emilla.command.app.AppCmdInfo;
import net.emilla.run.AppSuccess;
import net.emilla.run.DialogOffering;
import net.emilla.run.Offering;
import net.emilla.run.Success;
import net.emilla.run.TimePickerOffering;
import net.emilla.settings.Aliases;

import java.util.List;
import java.util.Set;

public abstract class EmillaCommand {

    public static final short
            DEFAULT = 0,
            CALL = 1,
            DIAL = 2,
            SMS = 3,
            EMAIL = 4,
            NAVIGATE = 5,
            LAUNCH = 6,
            SHARE = 7,
            SETTINGS = 8,
//            NOTE = ,
//            TODO = ,
            WEB = 9,
//            FIND = ,
            CLOCK = 10,
            ALARM = 11,
            TIMER = 12,
            POMODORO = 13,
            CALENDAR = 14,
            CONTACT = 15,
//            NOTIFY = ,
            CALCULATE = 16,
            WEATHER = 17,
            BOOKMARK = 18,
            TORCH = 19,
            INFO = 20,
            UNINSTALL = 21,
            TOAST = 22,
            DUPLICATE = 23;

    private static final int[] NAMES = {
            R.string.command_call,
            R.string.command_dial,
            R.string.command_sms,
            R.string.command_email,
            R.string.command_navigate,
            R.string.command_launch,
            R.string.command_share,
            R.string.command_settings,
        //    R.string.command_note,
        //    R.string.command_todo,
            R.string.command_web,
        //    R.string.command_find,
            R.string.command_clock,
            R.string.command_alarm,
            R.string.command_timer,
            R.string.command_pomodoro,
            R.string.command_calendar,
            R.string.command_contact,
        //    R.string.command_notify,
            R.string.command_calculate,
            R.string.command_weather,
            R.string.command_bookmark,
            R.string.command_torch,
            R.string.command_info,
            R.string.command_uninstall,
            R.string.command_toast
    };

    public static CmdTree tree(SharedPreferences prefs, Resources res,
            PackageManager pm, List<ResolveInfo> appList) {
        // todo: configurable aliasing
        // todo: edge case where a mapped app is uninstalled during the activity lifecycle
        CmdTree cmdTree = new CmdTree(res, appList.size());
        short i = 0;
        while (i < DUPLICATE - 1) {
            String lcName = res.getString(NAMES[i]).toLowerCase();
            Set<String> aliases = Aliases.set(prefs, res, i);
            cmdTree.putSingle(lcName, ++i);
            for (String alias : aliases) cmdTree.put(alias, i);
            // Todo: have separate set for multi-word aliases and use putSingle for the rest
        }
        i = 0;
        for (ResolveInfo ri : appList) {
            ActivityInfo actInfo = ri.activityInfo;
            CharSequence label = actInfo.loadLabel(pm);
            // TODO: there's the biggest performance bottleneck I've found thus far. Look into how the
            //  launcher caches labels for ideas on how to improve the performance of this critical
            //  onCreate task. That is, if they do to begin with (I can only assume..)
            AppCmdInfo cmdInfo = new AppCmdInfo(actInfo, pm, label);
            cmdTree.putApp(label, --i, cmdInfo, ~i);
            Set<String> aliases = Aliases.appSet(prefs, res, cmdInfo.pkg, cmdInfo.cls);
            if (aliases == null) continue;
            for (String alias : aliases) cmdTree.put(alias, i);
            // No need to pass app info again for aliases
            // Todo: have separate set for multi-word aliases and use putSingle for the rest
        }
        return cmdTree;
    }

    protected final AssistActivity activity;
    protected final Resources resources;
    protected final PackageManager pm;
    protected String mInstruction;

    protected EmillaCommand(AssistActivity act, String instruct) {
        activity = act;
        resources = act.getResources();
        pm = act.getPackageManager();
        mInstruction = instruct;
    }

    @CallSuper
    public void init() {
        baseInit();
    }

    public final void baseInit() {
        activity.updateLabel(title());
        activity.updateDetails(detailsId());
        activity.updateDataHint();
        activity.setImeAction(imeAction());
    }

    @CallSuper
    public void clean() {}

    protected void instruct(String instruction) {
        mInstruction = instruction;
    }

    void instructAppend(String data) {
        if (mInstruction == null) mInstruction = data;
        else mInstruction += '\n' + data;
    }

    protected String string(@StringRes int id) {
        return resources.getString(id);
    }

    protected String string(@StringRes int id, Object ... formatArgs) {
        return resources.getString(id, formatArgs);
    }

    protected String quantityString(@PluralsRes int id, int quantity) {
        return resources.getQuantityString(id, quantity, quantity);
    }

    protected String[] stringArray(@ArrayRes int id) {
        return resources.getStringArray(id);
    }

    protected String packageName() {
        return activity.getPackageName();
    }

    public void execute() {
        if (mInstruction == null) run();
        else run(mInstruction);
    }

    /**
     * Todo: these toast messages should generally be replaced with widget dialogs (which would have
     *  their own "more info please" chime). Excessive toasting is disruptive (the messages cover
     *  the keyboard and are opaque in many ROMs)
     *
     * @param text is shown as a toast notification at the bottom of the screen. Don't use
     *             hard-coded text.
     * @param longToast whether to use Toast.LENGTH_LONG. Use this sparingly, for reasons above.
     */
    protected void toast(CharSequence text, boolean longToast) {
        activity.toast(text, longToast);
    }

    protected void toast(CharSequence text) {
        activity.toast(text);
    }

    protected void chime(byte id) {
        activity.chime(id);
    }

    protected void giveAction(QuickAction action) {
        activity.addAction(action);
    }

    protected void reshowField(@IdRes int id) {
        activity.reshowField(id);
    }

    protected void hideField(@IdRes int id) {
        activity.hideField(id);
    }

    protected void removeAction(@IdRes int id) {
        activity.removeAction(id);
    }

    /**
     * This should be called any time a dialog is closed without calling {@link this#appSucceed(Intent)} to
     * reactivate the UI.
     *
     * @param chime whether to play a 'resume' sound if wanted
     */
    protected void onCloseDialog(boolean chime) {
        activity.onCloseDialog(chime);
    }

    /*======================================================================================*
     * IMPORTANT: One of the following methods should be called at the end of each command. *
     *======================================================================================*/

    /**
     * Completes the work that the user wants from the assistant and closes it. Typically entails
     * opening another app window, handing work to another program.
     * @param success finishes the work of the assistant.
     */
    protected void succeed(Success success) {
        activity.succeed(success);
    }

    /**
     * Tells the AssistActivity to close and start the `intent` activity. The succeeding activity must
     * never be excluded from the recents.
     *
     * @param intent is launched after the assistant closes. It's very important that this is
     *               resolvable, else an ANF exception will occur.
     */
    protected void appSucceed(Intent intent) {
        succeed(new AppSuccess(activity, intent));
    }

    /**
     * Todo: these toast messages should generally be replaced with widget dialogs (which would have
     *  their own "here you go" chime). Text displayed in toasts can't be copied, and excessive
     *  toasting is disruptive (the messages cover the keyboard and are opaque in many ROMs)
     *
     * @param text is shown as a toast notification at the bottom of the screen. Don't hard-code text.
     * @param longToast whether to use Toast.LENGTH_LONG. Use this sparingly, for reasons above.
     */
    protected void give(CharSequence text, boolean longToast) {
        activity.toast(text, longToast);
        activity.refreshInput();
        activity.onCloseDialog(false); // todo: revise handling and remove
    }

    /**
     * The user is "offered" a tool to complete their command with. Successful use of the tool should
     * lead to a prompt 'success', whereas canceled use should lead to a full reset of the command and
     * UI to their pre-offer state.
     *
     * @param offering the tool for the user.
     */
    protected void offer(Offering offering) {
        activity.offer(offering);
    }

    protected void offerDialog(AlertDialog.Builder builder) {
        offer(new DialogOffering(activity, builder));
    }

    protected void offerDialog(AlertDialog dialog) {
        offer(new DialogOffering(activity, dialog));
    }

    protected void offerTimePicker(TimePickerDialog dialog) {
        offer(new TimePickerOffering(activity, dialog));
    }

    /*==========================*
     * End of finisher methods. *
     *==========================*/

    @ArrayRes
    public int detailsId() {
        return -1;
    }

    public boolean usesData() {
        return false;
    }

    protected abstract CharSequence name();
    protected abstract CharSequence dupeLabel(); // Todo: replace with icons
    protected abstract CharSequence lcName();
    protected abstract CharSequence title();
    @DrawableRes
    public abstract int icon();
    public abstract int imeAction();
    // todo: you should be able to long-click the enter key in the command or data field to submit
    //  the command, using the action icon of one of the below.
    // requires changing the input method code directly

    protected abstract void run();
    /**
     * @param instruction is provided after in the command field after the command's name. It's always
     *                    space-trimmed should remain as such.
     */
    protected abstract void run(String instruction);
}
