package net.emilla.commands;

import static net.emilla.commands.EmillaCommand.Command.*;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.utils.Apps;

import java.util.List;
import java.util.Set;

public abstract class EmillaCommand {
public enum Command {
    DEFAULT,
    CALL,
    DIAL,
    SMS,
    EMAIL,
    LAUNCH,
    SHARE,
    SETTINGS,
    NOTE,
    TODO,
    WEB,
    FIND,
    CLOCK,
    ALARM,
    TIMER,
    POMODORO,
    CALENDAR,
    CONTACT,
    NOTIFY,
    CALCULATE,
    WEATHER,
    VIEW,
    TOAST,
    APP,
    APP_SEND,
    APP_SEND_DATA,
    APP_SEARCH,
    DUPLICATE
}

private static final int[] NAMES = {
    R.string.command_call,
    R.string.command_dial,
    R.string.command_sms,
    R.string.command_email,
    R.string.command_launch,
    R.string.command_share,
    R.string.command_settings,
    R.string.command_note,
    R.string.command_todo,
    R.string.command_web,
    R.string.command_find,
    R.string.command_clock,
    R.string.command_alarm,
    R.string.command_timer,
    R.string.command_pomodoro,
    R.string.command_calendar,
    R.string.command_contact,
    R.string.command_notify,
    R.string.command_calculate,
    R.string.command_weather,
    R.string.command_view,
    R.string.command_toast
};

private static AppCommand aliasMapped(final AssistActivity act, final CommandTree cmdTree,
        final Resources res, final String pkg, final int setId, final AppCommand appCmd) {
    final Set<String> aliases = act.prefs.getStringSet("aliases_" + pkg,
            Set.of(res.getStringArray(setId)));
    for (final String alias : aliases) cmdTree.putSingle(alias, appCmd, act);
    return appCmd;
}

@NonNull
private static AppCommand getAppCmd(final AssistActivity act, final PackageManager pm,
        final Resources res, final String pkg, final CommandTree cmdTree, final CharSequence label,
        final Intent launch) {
    final Intent send = Apps.sendTask(pkg);
    return switch (pkg) {
    case Apps.PKG_AOSP_CONTACTS -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_aosp_contacts,
            new AppSearchCommand(act, label, launch, Apps.searchTask(pkg), R.string.instruction_contact));
    case Apps.PKG_MARKOR -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_markor,
            new AppSendDataCommand(act, label, launch, send, R.string.instruction_text));
    case Apps.PKG_FIREFOX -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_firefox,
            new AppSearchCommand(act, label, launch, Apps.webSearchTask(pkg), R.string.instruction_web));
    case Apps.PKG_TOR -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_tor,
            new AppCommand(act, label, launch)); // Search/send intents are broken
    case Apps.PKG_SIGNAL -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_signal,
            new AppSendDataCommand(act, label, launch, send, R.string.instruction_message));
    case Apps.PKG_NEWPIPE -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_newpipe,
            new AppSendCommand(act, label, launch, send, R.string.instruction_video));
    case Apps.PKG_TUBULAR -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_tubular,
            new AppSendCommand(act, label, launch, send, R.string.instruction_video));
    case Apps.PKG_YOUTUBE -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_youtube,
            new AppSearchCommand(act, label, launch, Apps.searchTask(pkg), R.string.instruction_video));
    case Apps.PKG_DISCORD -> aliasMapped(act, cmdTree, res, pkg, R.array.aliases_discord,
            new AppSendCommand(act, label, launch, send, R.string.instruction_message));
    default -> send.resolveActivity(pm) == null ? new AppCommand(act, label, launch)
            : new AppSendCommand(act, label, launch, send);
    // Todo: generic AppSearchCommand in a way that handles conflicts with AppSendCommand
    };
}

public static CommandTree tree(final AssistActivity act, final SharedPreferences prefs,
        final Resources res, final PackageManager pm, final List<ResolveInfo> appList) {
    final Set<Command> deactivated = Set.of(NOTE, TODO, FIND, NOTIFY); // Todo
    // todo: configurable aliasing
    // todo: edge case where a mapped app is uninstalled during the activity lifecycle
    final CommandTree cmdTree = new CommandTree();
    final Command[] commands = values();
    int i = 0;
    while (i < NAMES.length) {
        final String lcName = res.getString(NAMES[i]).toLowerCase();
        final Set<String> aliases = Aliases.set(prefs, res, i);
        final Command enumCmd = commands[++i];
        if (deactivated.contains(enumCmd)) continue;
        final EmillaCommand cmd = instance(enumCmd, act);
        // Todo: return to the enumeration approach
        cmdTree.putSingle(lcName, cmd, act);
        for (final String alias : aliases) cmdTree.put(alias, cmd, act);
        // Todo: have separate set for multi-word aliases and use putSingle for the rest
    }
    for (final ResolveInfo ri : appList) {
        final ActivityInfo info = ri.activityInfo;
        final CharSequence label = info.loadLabel(pm);
        final String pkg = info.packageName;
        final Intent launch = Apps.launchIntent(pkg, info.name);
        final AppCommand appCmd = getAppCmd(act, pm, res, pkg, cmdTree, label, launch);
        final String lcLabel = label.toString().toLowerCase();
        cmdTree.put(lcLabel, appCmd, act);
    }
    cmdTree.putDefault(instance(DEFAULT, act));
    return cmdTree;
}

private static EmillaCommand instance(final Command cmd, final AssistActivity act) {
    return switch (cmd) {
    case DEFAULT -> new CommandWeb(act, DEFAULT); // TODO: make configurable
    case CALL -> new CommandCall(act);
    case DIAL -> new CommandDial(act);
    case SMS -> new CommandSms(act);
    case EMAIL -> new CommandEmail(act);
    case LAUNCH -> new CommandLaunch(act);
    case SHARE -> new CommandShare(act);
    case SETTINGS -> new CommandSettings(act);
    case NOTE -> new CommandNote(act);
    case TODO -> new CommandTodo(act);
    case WEB -> new CommandWeb(act, WEB);
    case FIND -> new CommandFind(act);
    case CLOCK -> new CommandClock(act);
    case ALARM -> new CommandAlarm(act);
    case TIMER -> new CommandTimer(act);
    case POMODORO -> new CommandPomodoro(act);
    case CALENDAR -> new CommandCalendar(act);
    case CONTACT -> new CommandContact(act);
    case NOTIFY -> new CommandNotify(act);
    case CALCULATE -> new CatCommandCalculate(act);
    case WEATHER -> new CatCommandWeather(act);
    case VIEW -> new CommandView(act);
    case TOAST -> new CommandToast(act);
    case APP, APP_SEND, APP_SEND_DATA, APP_SEARCH, DUPLICATE -> null; // uuuhhhhhhh
    };
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

public abstract Command cmd();
protected abstract CharSequence name();
protected abstract CharSequence dupeLabel(); // Todo: replace with icons
public abstract CharSequence lcName();
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
