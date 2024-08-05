package net.emilla.commands;

import static net.emilla.commands.EmillaCommand.Commands.*;
import static net.emilla.commands.EmillaCommand.DFLT_CMD;
import static java.lang.Character.charCount;
import static java.lang.Character.toChars;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.copyOfRange;
import static java.util.regex.Pattern.compile;

import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.commands.EmillaCommand.Commands;
import net.emilla.utils.Apps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * ideally (later probably), we'd preserve the state of traversal and do more limited computation
 * based on the indices at which text was edited (the tree contents should be considered immutable).
 * todo: upward & stateful traversals ^ some sort of mechanism for detecting token depth of the
 *  beginning char span
 */
public class CommandTree {
private static class CmdNode {
    private HashMap<String, CmdNode> map;
    private short cmd = DEFAULT;
    private short[] dupes;
    // Should also research database maintenence, and how to do so in a way that respects device
    //  storage and doesn't break in all number of risk cases (config change, device transfer, app
    //  uninstalls, etc.) If mapping can be sufficiently optimized, this won't even be necessary.
    //  But definitely look into how launcher apps keep track of all the installed apps without
    //  having to PM-query alllll that information on each boot (assuming that's not what it does..)
}

private final CmdNode root = new CmdNode();
private int depth = 0;
private final EmillaCommand[] mCoreCmds = new EmillaCommand[Commands.CORE_COUNT];
private final AppCmdInfo[] mAppCmdInfos;
private final AppCommand[] mAppCmds;

public CommandTree(final int appCount) {
    root.map = new HashMap<>();
    mAppCmdInfos = new AppCmdInfo[appCount];
    mAppCmds = new AppCommand[appCount];
}

/**
 * @param command is assumed not to have any leading spaces!
 * @return at most, the first three spans of non-word characters in the string as an array
 */
private String[] latinTokens(final String command, final boolean putting) {
    int dep = depth + 1;
    String[] tokens = new String[dep];
    final Matcher nonSpaces = compile("\\S+").matcher(command);
    for (int i = 0; i < dep; ++i) {
        if (!nonSpaces.find()) return copyOfRange(tokens, 0, i);
        tokens[i] = nonSpaces.group();
    }
    if (putting) while (nonSpaces.find()) { // todo: simplify in some way
        tokens = Arrays.copyOf(tokens, ++dep);
        tokens[dep - 1] = nonSpaces.group();
    }
    return tokens;
}

/**
 * @param command is assumed not to have any leading spaces!
 * @return at most, the first three codepoints in the string as a string-array
 */
private String[] characterTokens(final String command) { // TODO LANG: use the method
    final String[] tokens = new String[min(depth, command.codePointCount(0, command.length()))];
    for (int i = 0, offset = 0; i < tokens.length; ++i) {
        final int codePoint = command.codePointAt(offset);
        tokens[i] = String.valueOf(toChars(codePoint));
        offset += charCount(codePoint);
    }
    return tokens;
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
    int dep = 0;
    for (final String token : latinTokens(command, true)) {
        if (cur.map == null) cur.map = new HashMap<>();
        final CmdNode get = cur.map.get(token);
        if (get == null) {
            final CmdNode next = new CmdNode();
            cur.map.put(token, next);
            cur = next;
        } else cur = get;
        ++dep;
    }
    if (cur.cmd == DEFAULT) cur.cmd = id;
    else putDuplicate(cur, id);
    depth = max(depth, dep);
}

/**
 * Inserts an app command into the tree, which will load when the app's launcher label is typed into
 * the command field.
 *
 * @param label is the launcher shortcut title for the app command
 * @param id is a negative integer uniquely identifying this app command
 * @param info is used to generate an AppCommand instance in
 *             {@link this#singInstance(AssistActivity, short)}
 * @param idx must be the bitwise NOT of `id`, used to store `info` in the {@link this#mAppCmdInfos}
 *            array.
 */
public void putApp(final CharSequence label, final short id, final AppCmdInfo info, final int idx) {
    CmdNode cur = root;
    int dep = 0;
    for (final String token : latinTokens(label.toString().toLowerCase(), true)) {
        if (cur.map == null) cur.map = new HashMap<>();
        final CmdNode get = cur.map.get(token);
        if (get == null) {
            final CmdNode next = new CmdNode();
            cur.map.put(token, next);
            cur = next;
        } else cur = get;
        ++dep;
    }
    if (cur.cmd == DEFAULT) cur.cmd = id;
    else putDuplicate(cur, id);
    depth = max(depth, dep);
    mAppCmdInfos[idx] = info;
}

/**
 * Inserts a command into the first level of the tree. Must not be used for multi-word commands.
 *
 * @param command the command name. Must not contain whitespace for latin langs. Must not span more
 *                than one codepoint for character langs.
 *                Todo: could use int as token for codepoints, probably useless without a custom
 *                 hash-map though. Maybe in a C++ rewrite :P
 * @param id the command to map the token to
 */
public void putSingle(final String command, final short id) {
    final CmdNode get = root.map.get(command);
    if (get == null) {
        final CmdNode next = new CmdNode();
        next.cmd = id;
        root.map.put(command, next);
    } else if (get.cmd == DEFAULT) get.cmd = id;
    else putDuplicate(get, id);
    depth = max(depth, 1);
}

public EmillaCommand newCore(final AssistActivity act, final short id) {
    final EmillaCommand cmd = switch (id) {
        case DEFAULT -> new CommandWrapDefault(act, singInstance(act, DFLT_CMD));
        case CALL -> new CommandCall(act);
        case DIAL -> new CommandDial(act);
        case SMS -> new CommandSms(act);
        case EMAIL -> new CommandEmail(act);
        case LAUNCH -> new CommandLaunch(act);
        case SHARE -> new CommandShare(act);
        case SETTINGS -> new CommandSettings(act);
//        case NOTE -> new CommandNote(act);
//        case TODO -> new CommandTodo(act);
        case WEB -> new CommandWeb(act);
//        case FIND -> new CommandFind(act);
        case CLOCK -> new CommandClock(act);
        case ALARM -> new CommandAlarm(act);
        case TIMER -> new CommandTimer(act);
        case POMODORO -> new CommandPomodoro(act);
        case CALENDAR -> new CommandCalendar(act);
        case CONTACT -> new CommandContact(act);
//        case NOTIFY -> new CommandNotify(act);
        case CALCULATE -> new CatCommandCalculate(act);
        case WEATHER -> new CatCommandWeather(act);
        case VIEW -> new CommandView(act);
        case TOAST -> new CommandToast(act);
        default -> null;
    };
    mCoreCmds[id] = cmd;
    return cmd;
}

@NonNull
private static AppCommand newApp(final AssistActivity act, final AppCmdInfo info) {
    return switch (info.pkg) {
    case Apps.PKG_AOSP_CONTACTS -> new AppSearchCommand(act, info, R.string.instruction_contact);
    case Apps.PKG_MARKOR -> info.cls.equals(AppCmdInfo.CLS_MARKOR_MAIN)
            ? new AppSendDataCommand(act, info, R.string.instruction_text)
        : new AppCommand(act, info);
    // Markor can have multiple launchers. Only the main one should have the 'send' property.
    case Apps.PKG_FIREFOX -> new AppSearchCommand(act, info, R.string.instruction_web);
    case Apps.PKG_TOR -> new AppCommand(act, info);
    // Search/send intents are broken
    case Apps.PKG_SIGNAL -> new AppSendDataCommand(act, info, R.string.instruction_message);
    case Apps.PKG_NEWPIPE,
         Apps.PKG_TUBULAR -> new AppSendCommand(act, info, R.string.instruction_video);
    case Apps.PKG_YOUTUBE -> new AppSearchCommand(act, info, R.string.instruction_video);
    case Apps.PKG_DISCORD -> new AppSendCommand(act, info, R.string.instruction_message);
    default -> info.has_send ? new AppSendCommand(act, info) : new AppCommand(act, info);
    // Todo: generic AppSearchCommand in a way that handles conflicts with AppSendCommand
    };
}

private EmillaCommand singInstance(final AssistActivity act, final short id) {
    if (id >= DEFAULT) {
        return mCoreCmds[id] == null ? newCore(act, id) : mCoreCmds[id];
    }
    final int appId = ~id;
    final AppCommand cmd = mAppCmds[appId];
    return cmd == null ? newApp(act, mAppCmdInfos[appId]) : cmd;
}

private EmillaCommand instance(final AssistActivity act, final CmdNode node, final short id) {
    if (id == DUPLICATE) {
        final EmillaCommand[] cmds = new EmillaCommand[node.dupes.length];
        int i = -1;
        for (final short dupeId : node.dupes) cmds[++i] = singInstance(act, dupeId);
        return new DuplicateCommand(act, cmds);
        // Todo: maybe store these in a List if we don't want to call that constructor more than once
    }
    return singInstance(act, id);
}

private EmillaCommand restrainInstance(final AssistActivity act, final short id, final CmdNode node) {
    return id < 0 && mAppCmdInfos[~id].basic ? instance(act, node, DEFAULT) : instance(act, node, id);
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
 * Searches the tree for a mapping of `lcCommand`. If one is found, a new instance is created using
 * `act`, stored in {@link this#mCoreCmds}, and returned. Otherwise, the default command instance is
 * returned.
 *
 * @param act is used to generate new command instances when necessary
 * @param lcCommand must be lowercase!
 */
public EmillaCommand get(final AssistActivity act, final String lcCommand) {
    // TODO: why is this called twice?
    if (lcCommand.isBlank()) return singInstance(act, DEFAULT);

    CmdNode cur = root;
    short curCmd = DEFAULT;
    for (final String token : latinTokens(lcCommand, false)) {
        if (cur.map == null) return restrainInstance(act, curCmd, cur);
        final CmdNode get = cur.map.get(token);
        if (get == null) return restrainInstance(act, curCmd, cur);
        cur = get;
        curCmd = get.cmd == DEFAULT ? restrain(curCmd) : get.cmd;
    }
    return instance(act, cur, curCmd);
}
}
