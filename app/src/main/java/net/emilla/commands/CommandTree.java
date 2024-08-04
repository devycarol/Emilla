package net.emilla.commands;

import static java.lang.Character.charCount;
import static java.lang.Character.toChars;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.copyOfRange;
import static java.util.regex.Pattern.compile;

import net.emilla.AssistActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * ideally (later probably), we'd preserve the state of traversal and do more limited computation
 * based on the indices at which text was edited (the contents should be considered immutable).
 * todo: upward & stateful traversals ^ some sort of mechanism for detecting token depth of the
 *  beginning char span
 */
public class CommandTree {
private static class CmdNode {
    private HashMap<String, CmdNode> map;
    private EmillaCommand cmd;
    // Todo: I'd prefer this to be an enumeration rather than an object. Instantiating command
    //  objects en masse takes time, which is precious when mapping at every app start. This'd
    //  require using something like a short-int to map to commands in the original 'Enum' style,
    //  with something like negative values being used for custom/app commands.
    // Should also research database maintenence, and how to do so in a way that respects device
    //  storage and doesn't break in all number of risk cases (config change, device transfer, app
    //  uninstalls, etc.) If mapping can be sufficiently optimized, this won't even be necessary.
    //  But definitely look into how launcher apps keep track of all the installed apps without
    //  having to PM-query alllll that information on each boot (assuming that's not what it does..)
    // Could store an array of app command infos and retrieve from the bit-NOT of a negative num.
}

private final CmdNode root = new CmdNode();
private int depth = 0;

public CommandTree() {
    root.map = new HashMap<>();
}

public void putDefault(final EmillaCommand dfltCmd) {
    root.cmd = dfltCmd;
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

/**
 * Inserts a command into the tree, which will load once all the given tokens have been typed into
 * the command field.
 *
 * @param act {@link AssistActivity} instance for instantiating duplicate commands Todo: remove?
 */
public void put(final String command, final EmillaCommand cmd, final AssistActivity act) {
    // TODO: why does this map the first word of multi-word apps to their command??
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
    if (cur.cmd == null) cur.cmd = cmd;
    else cur.cmd = DuplicateCommand.instance(act, cur.cmd, cmd);
    depth = max(depth, dep);
}

/**
 * Inserts a command into the first level of the tree. Must not be used for multi-word commands.
 *
 * @param command the command name. Must not contain whitespace for latin langs. Must not span more
 *                than one codepoint for character langs.
 *                Todo: could use int as token for codepoints. Probably useless without
 *                 de-objectifying "HashMap" though. Maybe in the C++ rewrite :P
 * @param cmd the command to map the token to
 * @param act {@link AssistActivity} instance for instantiating duplicate commands Todo: remove?
 */
public void putSingle(final String command, final EmillaCommand cmd, final AssistActivity act) {
    final CmdNode get = root.map.get(command);
    if (get == null) {
        final CmdNode next = new CmdNode();
        next.cmd = cmd;
        root.map.put(command, next);
    } else if (get.cmd == null) get.cmd = cmd;
    else get.cmd = DuplicateCommand.instance(act, get.cmd, cmd);
    depth = max(depth, 1);
}

/**
 * If a command starts with something like an app name but is followed by more text, we want to
 * revert to the default command since the the instructions would be useless.
 *
 * @param cmd is queried on whether it's a non-instruction command.
 * @param dfltCmd is returned if `cmd` doesn't take instructions.
 * @return `cmd` if it's an instruction command, `dfltCmd` otherwise.
 */
private EmillaCommand restrain(final EmillaCommand cmd, final EmillaCommand dfltCmd) {
    // Todo: we may not want to fall entirely back to the default command in some cases. There's a
    //  good chance you just want to traverse up the tree one step. I.e. a one-word subcommand that
    //  yields to the parent command if more instruction is provided.
    //  Would entail parent pointers for the nodes, and `cmd` of the root being set to the dfltCmd
    return cmd instanceof AppCommand && !(cmd instanceof AppSendCommand
            || cmd instanceof AppSearchCommand) ? dfltCmd
        : cmd;
}

/**
 * @param command must be lowercase!
 */
public EmillaCommand get(final String command) { // TODO: why is this called twice?
    if (command.isBlank()) return root.cmd;
    CmdNode cur = root;
    EmillaCommand curCmd = root.cmd;
    for (final String token : latinTokens(command, false)) {
        if (cur.map == null) return restrain(curCmd, root.cmd);
        final CmdNode get = cur.map.get(token);
        if (get == null) return restrain(curCmd, root.cmd);
        cur = get;
        curCmd = get.cmd == null ? restrain(curCmd, root.cmd) : get.cmd;
    }
    return curCmd;
}
}
