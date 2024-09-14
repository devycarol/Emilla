package net.emilla.commands;

import static net.emilla.commands.EmillaCommand.Commands.*;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.commands.EmillaCommand.Commands;
import net.emilla.settings.SettingVals;
import net.emilla.utils.Apps;
import net.emilla.utils.Lang;

import java.util.HashMap;

/**
 * ideally (later probably), we'd preserve the state of traversal and do more limited computation
 * based on the indices at which text was edited (the tree contents should be considered immutable).
 * todo: upward & stateful traversals ^ some sort of mechanism for detecting token depth of the
 *  beginning char span
 */
public class CmdTree {
private static class CmdNode {
    private HashMap<String, CmdNode> map;
    private short cmd = DEFAULT;
    private short[] dupes;
}

private final Resources mRes;
private final CmdNode root = new CmdNode();
private final EmillaCommand[] mCoreCmds = new EmillaCommand[DUPLICATE];
private final AppCmdInfo[] mAppCmdInfos;
private final AppCommand[] mAppCmds;

public CmdTree(final Resources res, final int appCount) {
    mRes = res;
    root.map = new HashMap<>();
    mAppCmdInfos = new AppCmdInfo[appCount];
    mAppCmds = new AppCommand[appCount];
}

private static void putDuplicate(final CmdNode node, final short id) {
    // Todo: handle case where command "A: B foo bar" conflicts with "A B: foo bar"
    if (node.cmd == DUPLICATE) {
        final short[] dupes = new short[node.dupes.length + 1];
        System.arraycopy(node.dupes, 0, dupes, 0, node.dupes.length);
        dupes[node.dupes.length] = id;
        node.dupes = dupes;
    } else {
        node.dupes = new short[]{node.cmd, id};
        node.cmd = DUPLICATE;
    }
}

/**
 * Inserts a command into the tree, which will load once all the given tokens have been typed into
 * the command field.
 */
public void put(final String command, final short id) {
    CmdNode cur = root;
    for (final String token : Lang.cmdTokens(mRes, command)) {
        if (cur.map == null) cur.map = new HashMap<>();
        final CmdNode get = cur.map.get(token);
        if (get == null) {
            final CmdNode next = new CmdNode();
            cur.map.put(token, next);
            cur = next;
        } else cur = get;
    }
    if (cur.cmd == DEFAULT) cur.cmd = id;
    else putDuplicate(cur, id);
}

/**
 * Inserts an app command into the tree, which will load when the app's launcher label is typed into
 * the command field.
 *
 * @param label is the launcher shortcut title for the app command
 * @param id is a negative integer uniquely identifying this app command
 * @param info is used to generate an AppCommand instance in
 *             {@link this#singInstance(AssistActivity, short, String)}
 * @param idx must be the bitwise NOT of `id`, used to store `info` in the {@link this#mAppCmdInfos}
 *            array.
 */
public void putApp(final CharSequence label, final short id, final AppCmdInfo info, final int idx) {
    CmdNode cur = root;
    for (final String token : Lang.cmdTokens(mRes, label.toString())) {
        if (cur.map == null) cur.map = new HashMap<>();
        final CmdNode get = cur.map.get(token);
        if (get == null) {
            final CmdNode next = new CmdNode();
            cur.map.put(token, next);
            cur = next;
        } else cur = get;
    }
    if (cur.cmd == DEFAULT) cur.cmd = id;
    else putDuplicate(cur, id);
    mAppCmdInfos[idx] = info;
}

/**
 * Inserts a command into the first level of the tree. Must not be used for multi-word commands.
 *
 * @param lcName the command name. Must not contain whitespace for latin langs. Must not span more
 *                than one codepoint for character langs.
 *                Todo: could use int as token for codepoints, probably useless without a custom
 *                 hash-map though. Maybe in a C++ rewrite :P
 * @param id the command to map the token to
 */
public void putSingle(final String lcName, final short id) {
    final CmdNode get = root.map.get(lcName);
    if (get == null) {
        final CmdNode next = new CmdNode();
        next.cmd = id;
        root.map.put(lcName, next);
    } else if (get.cmd == DEFAULT) get.cmd = id;
    else putDuplicate(get, id);
}

public EmillaCommand newCore(final AssistActivity act, final short id, final String instruct) {
    final EmillaCommand cmd = switch (id) {
        case DEFAULT -> {
            final short dfltCmd = SettingVals.defaultCommand(act.prefs());
            yield new CommandWrapDefault(act, instruct, singInstance(act, dfltCmd, instruct));
        }
        case CALL -> new CommandCall(act, instruct);
        case DIAL -> new CommandDial(act, instruct);
        case SMS -> new CommandSms(act, instruct);
        case EMAIL -> new CommandEmail(act, instruct);
        case NAVIGATE -> new CatCommandNavigate(act, instruct);
        case LAUNCH -> new CommandOpenLaunch(act, instruct);
        case SHARE -> new CommandShare(act, instruct);
        case SETTINGS -> new CommandSettings(act, instruct);
//        case NOTE -> new CommandNote(act, instruct);
//        case TODO -> new CommandTodo(act, instruct);
        case WEB -> new CommandWeb(act, instruct);
//        case FIND -> new CommandFind(act, instruct);
        case CLOCK -> new CommandClock(act, instruct);
        case ALARM -> new CommandAlarm(act, instruct);
        case TIMER -> new CommandTimer(act, instruct);
        case POMODORO -> new CommandPomodoro(act, instruct);
        case CALENDAR -> new CommandCalendar(act, instruct);
        case CONTACT -> new CommandContact(act, instruct);
//        case NOTIFY -> new CommandNotify(act, instruct);
        case CALCULATE -> new CatCommandCalculate(act, instruct);
        case WEATHER -> new CatCommandWeather(act, instruct);
        case VIEW -> new CommandView(act, instruct);
        case INFO -> new CommandOpenInfo(act, instruct);
        case UNINSTALL -> new CommandOpenUninstall(act, instruct);
        case TOAST -> new CommandToast(act, instruct);
        default -> null;
    };
    mCoreCmds[id] = cmd;
    return cmd;
}

@NonNull
private static AppCommand newApp(final AssistActivity act, final AppCmdInfo info,
        final String instruct) {
    return switch (info.pkg) {
    case Apps.PKG_AOSP_CONTACTS -> new AppSearchCommand(act, instruct, info, R.string.instruction_contact);
    case Apps.PKG_MARKOR -> info.cls.equals(AppCmdInfo.CLS_MARKOR_MAIN)
            ? new AppSendDataCommand(act, instruct, info, R.string.instruction_text)
        : new AppCommand(act, instruct, info);
    // Markor can have multiple launchers. Only the main one should have the 'send' property.
    case Apps.PKG_FIREFOX -> new AppSearchCommand(act, instruct, info, R.string.instruction_web);
    case Apps.PKG_TOR -> new AppCommand(act, instruct, info);
    // Tor search/send intents are broken.
    case Apps.PKG_SIGNAL -> new AppSendDataCommand(act, instruct, info, R.string.instruction_message);
    case Apps.PKG_NEWPIPE,
         Apps.PKG_TUBULAR -> new AppSendCommand(act, instruct, info, R.string.instruction_video);
    case Apps.PKG_GITHUB -> new AppSendDataCommand(act, instruct, info, R.string.instruction_issues);
    case Apps.PKG_YOUTUBE -> new AppSearchCommand(act, instruct, info, R.string.instruction_video);
    case Apps.PKG_DISCORD -> new AppSendCommand(act, instruct, info, R.string.instruction_message);
    default -> info.has_send ? new AppSendCommand(act, instruct, info) : new AppCommand(act, instruct, info);
    // Todo: generic AppSearchCommand in a way that handles conflicts with AppSendCommand
    };
}

private EmillaCommand singInstance(final AssistActivity act, final short id, final String instruct) {
    if (id >= DEFAULT) {
        if (mCoreCmds[id] == null) return newCore(act, id, instruct);
        mCoreCmds[id].instruct(instruct);
        return mCoreCmds[id];
    }
    final int appId = ~id;
    if (mAppCmds[appId] == null) return mAppCmds[appId] = newApp(act, mAppCmdInfos[appId], instruct);
    mAppCmds[appId].instruct(instruct);
    return mAppCmds[appId];
}

private EmillaCommand instance(final AssistActivity act, final CmdNode node, final short id,
        final String instruct) {
    if (id == DUPLICATE) {
        final EmillaCommand[] cmds = new EmillaCommand[node.dupes.length];
        int i = -1;
        for (final short dupeId : node.dupes) cmds[++i] = singInstance(act, dupeId, instruct);
        return new DuplicateCommand(act, instruct, cmds);
        // Todo: maybe store these in a List if we don't want to call that constructor more than once
    }
    return singInstance(act, id, instruct);
}

private EmillaCommand restrainInstance(final AssistActivity act, final short id, final CmdNode node,
        final String instruct) {
    return id < 0 && mAppCmdInfos[~id].basic ? instance(act, node, DEFAULT, instruct)
            : instance(act, node, id, instruct);
}

/**
 * If a command starts with something like an app name but is followed by more text, we want to
 * revert to the default command since the the instructions would be useless.
 *
 * @param id is queried on whether it's a non-instruction command.
 * @return `id` if it's an instruction command, {@link Commands#DEFAULT} otherwise.
 */
private short restrain(final short id) {
    // Todo: we may not want to fall entirely back to the default command in some cases. There's a
    //  good chance you just want to traverse up the tree one step. I.e. a one-word subcommand that
    //  yields to the parent command if more instruction is provided.
    //  Would entail parent pointers for the nodes, and `cmd` of the root being set to the dfltCmd
    return id < 0 && mAppCmdInfos[~id].basic ? DEFAULT : id;
}

/**
 * Searches the tree for a mapping of `command`. If one is found, a new instance is created using
 * `act`, stored in {@link this#mCoreCmds}, and returned. Otherwise, the default command instance is
 * returned.
 *
 * @param act is used to generate new command instances when necessary
 */
public EmillaCommand get(final AssistActivity act, final String command) {
    // Todo: why is this called twice sometimes? Is it because of rich input stuff?
    if (command.isBlank()) return singInstance(act, DEFAULT, null);

    CmdNode cur = root;
    short curCmd = DEFAULT;
    final CmdTokens tokens = Lang.cmdTokens(mRes, command);
    for (final String token : tokens) {
        if (cur.map == null) return restrainInstance(act, curCmd, cur, tokens.instruct());
        final CmdNode get = cur.map.get(token);
        if (get == null) return restrainInstance(act, curCmd, cur, tokens.instruct());
        cur = get;
        curCmd = get.cmd == DEFAULT ? restrain(curCmd) : get.cmd;
        tokens.nextInstruct();
    }
    return instance(act, cur, curCmd, tokens.instruct());
}
}
