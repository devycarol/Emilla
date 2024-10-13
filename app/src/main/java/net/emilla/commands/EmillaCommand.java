package net.emilla.commands;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

import java.util.List;
import java.util.Set;

public abstract class EmillaCommand {
public static class Commands {
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
//        NOTE = ,
//        TODO = ,
        WEB = 9,
//        FIND = ,
        CLOCK = 10,
        ALARM = 11,
        TIMER = 12,
        POMODORO = 13,
        CALENDAR = 14,
        CONTACT = 15,
//        NOTIFY = ,
        CALCULATE = 16,
        WEATHER = 17,
        VIEW = 18,
        INFO = 19,
        UNINSTALL = 20,
        TOAST = 21,
        DUPLICATE = 22;
}

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
    R.string.command_view,
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
    while (i < Commands.DUPLICATE - 1) {
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

private final AssistActivity mActivity;
private final Resources mResources;
protected String mInstruction;

protected EmillaCommand(AssistActivity act, String instruct) {
    mActivity = act;
    mResources = act.getResources();
    mInstruction = instruct;
}

@CallSuper
public void init() {
    mActivity.updateLabel(title());
    mActivity.updateDetails(detailsId());
    mActivity.updateDataHint();
    mActivity.setImeAction(imeAction());
}

@CallSuper
public void clean() {}

void instruct(String instruction) {
    mInstruction = instruction;
}

void instructAppend(String data) {
    if (mInstruction == null) mInstruction = data;
    else mInstruction += '\n' + data;
}

protected AssistActivity activity() {
    return mActivity;
}

protected Resources resources() {
    return mResources;
}

protected String string(@StringRes int id) {
    return mResources.getString(id);
}

protected String string(@StringRes int id, Object ... formatArgs) {
    return mResources.getString(id, formatArgs);
}

protected String[] stringArray(@ArrayRes int id) {
    return mResources.getStringArray(id);
}

protected PackageManager packageManager() {
    return mActivity.getPackageManager();
}

protected String packageName() {
    return mActivity.getPackageName();
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
    mActivity.toast(text, longToast);
}

protected void giveAction(@IdRes int actionId, @StringRes int descriptionId,
        @DrawableRes int iconId, View.OnClickListener click) {
    mActivity.addAction(actionId, string(descriptionId), iconId, click);
}

protected void giveFieldToggle(@IdRes int actionId, @StringRes int fieldNameId,
        @DrawableRes int iconId, View.OnClickListener click) {
    // Todo acc: the spoken description should update on toggle
    mActivity.addAction(actionId, string(R.string.action_field, string(fieldNameId)),
            iconId, click);
}

protected boolean toggleField(@IdRes int fieldId, @StringRes int hintId,
        boolean focus) {
    return mActivity.toggleField(fieldId, hintId, focus);
}

protected String fieldText(@IdRes int fieldId) {
    return mActivity.getFieldText(fieldId);
}

protected void hideField(@IdRes int fieldId) {
    mActivity.hideField(fieldId);
}

protected void removeAction(@IdRes int actionId) {
    mActivity.removeAction(actionId);
}

/**
 * This should be called any time a dialog is closed without calling {@link this#succeed(Intent)} to
 * reactivate the UI.
 *
 * @param chime whether to play a 'resume' sound if wanted
 */
protected void onCloseDialog(boolean chime) {
    mActivity.onCloseDialog(chime);
}

/*======================================================================================*
 * IMPORTANT: One of the following methods should be called at the end of each command. *
 *======================================================================================*/

/**
 * Tells the AssistActivity to close, start the `intent` activity, and play a 'success' chime if
 * wanted. The succeeding activity must never be excluded from the recents.
 *
 * @param intent is launched after the assistant closes. It's very important that this is
 *               resolvable, else an ANF exception will occur.
 */
protected void succeed(Intent intent) {
    mActivity.succeed(intent);
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
    mActivity.toast(text, longToast);
    mActivity.refreshInput();
    mActivity.onCloseDialog(false); // todo: revise handling and remove
}

/**
 * The user is shown an input dialog and a 'pending' chime is played if wanted. Successful entry
 * must call {@link this#succeed(Intent)}.
 * Canceled input must call {@link AssistActivity#onCloseDialog(boolean)} and reset the command to
 * its pre-submission state.
 *
 * @param dialog is shown to the user. Please ensure these are easy to use with screen readers
 *               and other assistive technology.
 */
protected void offer(AlertDialog dialog) {
    mActivity.offer(dialog);
}

/**
 * Tells the AssistActivity to start the `intent` activity for a result, playing a 'pend' chime if
 * wanted. The offered activity should be comfortably placed on top of the assistant's task.
 *
 * @param intent should be "offering" in nature, such as a selection screen. Don't use
 *               {@link Intent#FLAG_ACTIVITY_NEW_TASK}.
 */
protected void offer(Intent intent, int requestCode) {
    mActivity.offer(intent, requestCode);
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
@DrawableRes public abstract int icon();
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
