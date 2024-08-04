package net.emilla.commands;

import static android.view.inputmethod.EditorInfo.*;
import static net.emilla.commands.EmillaCommand.Command.*;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.utils.Apps;

import java.util.HashMap;
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

private static final Command DEFAULT_CMD = WEB; // todo: make configurable

private static AppCommand aliasMapped(final AssistActivity act, final CommandTree cmdTree,
        final SharedPreferences prefs, final Resources res, final String pkg, final int setId,
        final AppCommand appCmd) {
    final Set<String> aliases = prefs.getStringSet("aliases_" + pkg,
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
        case Apps.PKG_AOSP_CONTACTS -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_aosp_contacts,
                new AppSearchCommand(act, label, launch, Apps.searchTask(pkg), R.string.instruction_contact));
        case Apps.PKG_MARKOR -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_markor,
                new AppSendDataCommand(act, label, launch, send, R.string.instruction_text));
        case Apps.PKG_FIREFOX -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_firefox,
                new AppSearchCommand(act, label, launch, Apps.webSearchTask(pkg), R.string.instruction_web));
        case Apps.PKG_TOR -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_tor,
                new AppCommand(act, label, launch)); // Search/send intents are broken
        case Apps.PKG_SIGNAL -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_signal,
                new AppSendDataCommand(act, label, launch, send, R.string.instruction_message));
        case Apps.PKG_NEWPIPE -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_newpipe,
                new AppSendCommand(act, label, launch, send, R.string.instruction_video));
        case Apps.PKG_TUBULAR -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_tubular,
                new AppSendCommand(act, label, launch, send, R.string.instruction_video));
        case Apps.PKG_YOUTUBE -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_youtube,
                new AppSearchCommand(act, label, launch, Apps.searchTask(pkg), R.string.instruction_video));
        case Apps.PKG_DISCORD -> aliasMapped(act, cmdTree, act.prefs, res, pkg, R.array.aliases_discord,
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

public static Command command(
        final HashMap<CharSequence, Command> commandMap,
        final HashMap<CharSequence, EmillaCommand> appMap,
        final CharSequence name,
        final boolean noSpace
) {
    final String lcName = name.toString().toLowerCase();
    final Command coreCmd = commandMap.get(lcName);
    if (coreCmd == null) {
        final EmillaCommand cmdInst = appMap.get(lcName);
        if (cmdInst == null) return DEFAULT;
        if (cmdInst instanceof AppSendCommand || cmdInst instanceof AppSearchCommand) return cmdInst.cmd();
        if (cmdInst instanceof AppCommand) return noSpace ? APP : DEFAULT; // todo: space separation no good
        return DUPLICATE;
    }
    return coreCmd;
}

public static boolean usesData(final Command cmd) {
    return switch (cmd) {
    case DEFAULT -> usesData(DEFAULT_CMD);
    case CALL, DIAL, LAUNCH, SETTINGS, WEB, FIND, CLOCK, CALCULATE, WEATHER, VIEW, APP_SEND, APP,
            APP_SEARCH -> false;
    case SMS, EMAIL, SHARE, NOTE, TODO, ALARM, TIMER, POMODORO, CALENDAR, CONTACT, NOTIFY, TOAST,
            DUPLICATE, APP_SEND_DATA -> true;
    };
}

public static int imeAction(final Command cmd) {
    // todo: you should be able to long-click the enter key in the command or data field to submit the command, using the action icon of one of the below.
    // requires changing the input method code directly
    return switch (cmd) {
    case DEFAULT -> imeAction(DEFAULT_CMD);
    case CALL, DIAL, LAUNCH, WEATHER, VIEW, APP -> IME_ACTION_GO;
    case WEB, FIND, APP_SEARCH -> IME_ACTION_SEARCH;
    case APP_SEND -> IME_ACTION_SEND; // todo: this shouldn't apply when just launching and also not to the newpipes
    case SETTINGS, CLOCK, CALCULATE -> IME_ACTION_DONE;
    case SMS, EMAIL, SHARE, NOTE, TODO, ALARM, TIMER, POMODORO, CALENDAR, CONTACT, NOTIFY, TOAST,
            DUPLICATE, APP_SEND_DATA -> IME_ACTION_NEXT;
    // This is used for ANY DATA COMMAND
    };
}

private static boolean shouldLowercase(final Command cmd) {
    // Proper names and initialisms need not be lowercased mid-sentence.
    // Please let me know if this should vary for your locale.
    return switch (cmd) {
    case DEFAULT, CALL, DIAL, EMAIL, LAUNCH, SHARE, SETTINGS, NOTE, TODO, WEB, FIND, CLOCK,
            ALARM, TIMER, POMODORO, CALENDAR, CONTACT, NOTIFY, CALCULATE, WEATHER, VIEW, TOAST,
            DUPLICATE -> true;
    case SMS, APP, APP_SEND, APP_SEND_DATA, APP_SEARCH -> false;
    };
}

private static int detailsId(final Command cmd) {
    return switch (cmd) {
    case DEFAULT -> detailsId(DEFAULT_CMD);
    case CALL, DIAL -> R.array.details_call_phone;
    case SMS -> R.array.details_sms;
    case EMAIL -> R.array.details_email;
    case SHARE -> R.array.details_share;
    case SETTINGS -> R.array.details_settings;
    case NOTE -> R.array.details_note;
    case ALARM -> R.array.details_alarm;
    case TIMER -> R.array.details_timer;
    case POMODORO -> R.array.details_pomodoro;
    case CALENDAR -> R.array.details_calendar;
    case CONTACT -> R.array.details_contact;
    case TOAST -> R.array.details_toast;
    case DUPLICATE -> R.array.details_duplicate;
    case APP_SEND -> R.array.details_app_send; // todo: shouldn't apply to newpipes
    case APP_SEND_DATA -> R.array.details_app_send_data;
    case LAUNCH, TODO, WEB, FIND, CLOCK, NOTIFY, CALCULATE, WEATHER, VIEW, APP, APP_SEARCH -> -1;
    };
}

public static CharSequence details(final Resources res, final Command command) {
    final int detailsId = detailsId(command);
    return detailsId == -1 ? null : String.join("\n\n", res.getStringArray(detailsId));
}

public static CharSequence dataHint(final Resources res, final Command cmd) {
    final int hintId = switch (cmd) {
    case SMS -> R.string.data_hint_sms;
    case EMAIL -> R.string.data_hint_email;
    case SHARE -> R.string.data_hint_share;
    case NOTE -> R.string.data_hint_note;
    case TODO -> R.string.data_hint_todo;
    case ALARM -> R.string.data_hint_alarm;
    case TIMER -> R.string.data_hint_timer;
    case POMODORO -> R.string.data_hint_pomodoro;
    case CALENDAR -> R.string.data_hint_calendar;
    case CONTACT -> R.string.data_hint_contact;
    case NOTIFY -> R.string.data_hint_notify;
    case TOAST -> R.string.data_hint_toast;
    case APP_SEND_DATA -> R.string.data_hint_app_send_data;
    case DEFAULT, CALL, DIAL, LAUNCH, SETTINGS, WEB, FIND, CLOCK, CALCULATE, WEATHER, VIEW,
            DUPLICATE, APP, APP_SEND, APP_SEARCH -> R.string.data_hint_default;
    };
    return res.getString(hintId);
}

public static int icon(Command cmd) {
    return switch (cmd) {
    case DEFAULT -> icon(DEFAULT_CMD);
    case CALL -> R.drawable.ic_call;
    case DIAL -> R.drawable.ic_dial;
    case SMS -> R.drawable.ic_sms;
    case EMAIL -> R.drawable.ic_email;
    case LAUNCH -> R.drawable.ic_launch;
    case SHARE -> R.drawable.ic_share;
    case SETTINGS -> R.drawable.ic_settings;
    case NOTE -> R.drawable.ic_note;
    case TODO -> R.drawable.ic_todo;
    case WEB -> R.drawable.ic_web;
    case FIND -> R.drawable.ic_find;
    case CLOCK -> R.drawable.ic_clock;
    case ALARM -> R.drawable.ic_alarm;
    case TIMER -> R.drawable.ic_timer;
    case POMODORO -> R.drawable.ic_pomodoro;
    case CALENDAR -> R.drawable.ic_calendar;
    case CONTACT -> R.drawable.ic_contact;
    case NOTIFY -> R.drawable.ic_notify;
    case CALCULATE -> R.drawable.ic_calculate;
    case WEATHER -> R.drawable.ic_weather;
    case VIEW -> R.drawable.ic_view;
    case TOAST -> R.drawable.ic_toast;
    case DUPLICATE -> R.drawable.ic_command;
    case APP, APP_SEND, APP_SEND_DATA, APP_SEARCH -> R.drawable.ic_app; // TODO: use launcher icons
    };
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
    case WEB -> new CommandWeb(act, cmd);
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

public static EmillaCommand instance(final AssistActivity act, final Command cmd,
        final CharSequence fullCommand, final CharSequence name, final HashMap<CharSequence, EmillaCommand> appMap) {
    return switch (cmd) {
    case DEFAULT -> instance(act, DEFAULT_CMD, fullCommand, name, appMap);
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
    case DUPLICATE, APP_SEND, APP_SEND_DATA, APP_SEARCH -> appMap.get(name.toString().toLowerCase());
    case APP -> {
        final EmillaCommand appCmd = appMap.get(fullCommand.toString().toLowerCase());
        yield appCmd == null ? instance(act, DEFAULT_CMD, fullCommand, name, appMap)
                : appCmd;
    }};
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

public abstract Command cmd();
protected abstract CharSequence name();
protected abstract CharSequence dupeLabel(); // Todo: replace with icons
public abstract CharSequence lcName();
public abstract CharSequence title();

public abstract void run();
/**
 * @param instruction is provided after in the command field after the command's name. It's always
 *                    space-trimmed should remain as such.
 */
public abstract void run(String instruction);
}
