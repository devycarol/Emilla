package net.emilla.commands;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;

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
        LAUNCH = 5,
        SHARE = 6,
        SETTINGS = 7,
//        NOTE = ,
//        TODO = ,
        WEB = 8,
//        FIND = ,
        CLOCK = 9,
        ALARM = 10,
        TIMER = 11,
        POMODORO = 12,
        CALENDAR = 13,
        CONTACT = 14,
//        NOTIFY = ,
        CALCULATE = 15,
        WEATHER = 16,
        VIEW = 17,
        INFO = 18,
        TOAST = 19,
        DUPLICATE = 20;
}

private static final int[] NAMES = {
    R.string.command_call,
    R.string.command_dial,
    R.string.command_sms,
    R.string.command_email,
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
    R.string.command_toast
};

public static CommandTree tree(final SharedPreferences prefs, final Resources res,
        final PackageManager pm, final List<ResolveInfo> appList) {
    // todo: configurable aliasing
    // todo: edge case where a mapped app is uninstalled during the activity lifecycle
    final CommandTree cmdTree = new CommandTree(appList.size());
    short i = 0;
    while (i < Commands.DUPLICATE - 1) {
        final String lcName = res.getString(NAMES[i]).toLowerCase();
        final Set<String> aliases = Aliases.set(prefs, res, i);
        cmdTree.putSingle(lcName, ++i);
        for (final String alias : aliases) cmdTree.put(alias, i);
        // Todo: have separate set for multi-word aliases and use putSingle for the rest
    }
    i = 0;
    for (final ResolveInfo ri : appList) {
        final ActivityInfo actInfo = ri.activityInfo;
        final CharSequence label = actInfo.loadLabel(pm);
        // TODO: there's the biggest performance bottleneck I've found thus far. Look into how the
        //  launcher caches labels for ideas on how to improve the performance of this critical
        //  onCreate task. That is, if they do to begin with (I can only assume..)
        final AppCmdInfo cmdInfo = new AppCmdInfo(actInfo, pm, label);
        cmdTree.putApp(label, --i, cmdInfo, ~i);
        final Set<String> aliases = Aliases.appSet(prefs, res, cmdInfo.pkg, cmdInfo.cls);
        if (aliases == null) continue;
        for (final String alias : aliases) cmdTree.put(alias, i);
        // No need to pass app info again for aliases
        // Todo: have separate set for multi-word aliases and use putSingle for the rest
    }
    return cmdTree;
}

private final AssistActivity mActivity;

protected EmillaCommand(final AssistActivity act) {
    mActivity = act;
}

protected AssistActivity activity() {
    return mActivity;
}

protected Resources resources() {
    return mActivity.getResources();
}

protected PackageManager packageManager() {
    return mActivity.getPackageManager();
}

protected String packageName() {
    return mActivity.getPackageName();
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
protected void toast(final CharSequence text, final boolean longToast) {
    mActivity.toast(text, longToast);
}

/**
 * This should be called any time a dialog is closed without calling {@link this#succeed(Intent)} to
 * reactivate the UI.
 *
 * @param chime whether to play a 'resume' sound if wanted
 */
protected void onCloseDialog(final boolean chime) {
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
protected void succeed(final Intent intent) {
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
protected void give(final CharSequence text, final boolean longToast) {
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
protected void offer(final AlertDialog dialog) {
    mActivity.offer(dialog);
}

/**
 * Tells the AssistActivity to start the `intent` activity for a result, playing a 'pend' chime if
 * wanted. The offered activity should be comfortably placed on top of the assistant's task.
 *
 * @param intent should be "offering" in nature, such as a selection screen. Don't use
 *               {@link Intent#FLAG_ACTIVITY_NEW_TASK}.
 */
protected void offer(final Intent intent, final int requestCode) {
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
public abstract CharSequence title();
@DrawableRes public abstract int icon();
public abstract int imeAction();
// todo: you should be able to long-click the enter key in the command or data field to submit
//  the command, using the action icon of one of the below.
// requires changing the input method code directly

public abstract void run();
/**
 * @param instruction is provided after in the command field after the command's name. It's always
 *                    space-trimmed should remain as such.
 */
public abstract void run(final String instruction);
}
