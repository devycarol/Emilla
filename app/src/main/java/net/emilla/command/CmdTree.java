package net.emilla.command;

import static net.emilla.command.EmillaCommand.*;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.command.app.AospContacts;
import net.emilla.command.app.AppCommand;
import net.emilla.command.app.AppCommand.AppInfo;
import net.emilla.command.app.AppSend;
import net.emilla.command.app.Discord;
import net.emilla.command.app.Firefox;
import net.emilla.command.app.Github;
import net.emilla.command.app.Markor;
import net.emilla.command.app.Newpipe;
import net.emilla.command.app.Outlook;
import net.emilla.command.app.Signal;
import net.emilla.command.app.Tor;
import net.emilla.command.app.Tubular;
import net.emilla.command.app.Youtube;
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bookmark;
import net.emilla.command.core.Calculate;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.Dial;
import net.emilla.command.core.Email;
import net.emilla.command.core.Info;
import net.emilla.command.core.Launch;
import net.emilla.command.core.Navigate;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.Settings;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Time;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Uninstall;
import net.emilla.command.core.Weather;
import net.emilla.command.core.Web;
import net.emilla.lang.Lang;
import net.emilla.settings.SettingVals;

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
    private final AppInfo[] mAppInfos;
    private final AppCommand[] mAppCmds;

    public CmdTree(Resources res, int appCount) {
        mRes = res;
        root.map = new HashMap<>();
        mAppInfos = new AppInfo[appCount];
        mAppCmds = new AppCommand[appCount];
    }

    private static void putDuplicate(CmdNode node, short id) {
        // Todo: handle case where command "A: B foo bar" conflicts with "A B: foo bar"
        if (node.cmd == DUPLICATE) {
            short[] dupes = new short[node.dupes.length + 1];
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
    public void put(String command, short id) {
        CmdNode cur = root;
        for (String token : Lang.cmdTokens(mRes, command)) {
            if (cur.map == null) cur.map = new HashMap<>();
            CmdNode get = cur.map.get(token);
            if (get == null) {
                CmdNode next = new CmdNode();
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
     *               {@link this#singInstance(AssistActivity, short, String)}
     * @param idx must be the bitwise NOT of `id`, used to store `info` in the
     *            {@link this#mAppInfos} array.
     */
    public void putApp(CharSequence label, short id, AppInfo info, int idx) {
        CmdNode cur = root;
        for (String token : Lang.cmdTokens(mRes, label.toString())) {
            if (cur.map == null) cur.map = new HashMap<>();
            CmdNode get = cur.map.get(token);
            if (get == null) {
                CmdNode next = new CmdNode();
                cur.map.put(token, next);
                cur = next;
            } else cur = get;
        }
        if (cur.cmd == DEFAULT) cur.cmd = id;
        else putDuplicate(cur, id);
        mAppInfos[idx] = info;
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
    public void putSingle(String lcName, short id) {
        CmdNode get = root.map.get(lcName);
        if (get == null) {
            CmdNode next = new CmdNode();
            next.cmd = id;
            root.map.put(lcName, next);
        } else if (get.cmd == DEFAULT) get.cmd = id;
        else putDuplicate(get, id);
    }

    public EmillaCommand newCore(AssistActivity act, short id, String instruct) {
        EmillaCommand cmd = switch (id) {
            case DEFAULT -> {
                short dfltCmd = SettingVals.defaultCommand(act.prefs());
                yield new DefaultCommandWrapper(act, instruct, singInstance(act, dfltCmd, instruct));
            }
            case CALL -> new Call(act, instruct);
            case DIAL -> new Dial(act, instruct);
            case SMS -> new Sms(act, instruct);
            case EMAIL -> new Email(act, instruct);
            case NAVIGATE -> new Navigate(act, instruct);
            case LAUNCH -> new Launch(act, instruct);
            case COPY -> new Copy(act, instruct);
            case SHARE -> new Share(act, instruct);
            case SETTINGS -> new Settings(act, instruct);
    //        case NOTE -> new CommandNote(act, instruct);
    //        case TODO -> new CommandTodo(act, instruct);
            case WEB -> new Web(act, instruct);
    //        case FIND -> new CommandFind(act, instruct);
            case TIME -> new Time(act, instruct);
            case ALARM -> new Alarm(act, instruct);
            case TIMER -> new Timer(act, instruct);
            case POMODORO -> new Pomodoro(act, instruct);
            case CALENDAR -> new Calendar(act, instruct);
            case CONTACT -> new Contact(act, instruct);
    //        case NOTIFY -> new CommandNotify(act, instruct);
            case CALCULATE -> new Calculate(act, instruct);
            case WEATHER -> new Weather(act, instruct);
            case BOOKMARK -> new Bookmark(act, instruct);
            case TORCH -> new Torch(act, instruct);
            case INFO -> new Info(act, instruct);
            case UNINSTALL -> new Uninstall(act, instruct);
            case TOAST -> new Toast(act, instruct);
            default -> null;
        };
        mCoreCmds[id] = cmd;
        return cmd;
    }

    @NonNull
    private static AppCommand newApp(AssistActivity act, String instruct, AppInfo info) {
        return switch (info.pkg) {
            case AospContacts.PKG -> new AospContacts(act, instruct, info);
            case Markor.PKG -> Markor.instance(act, instruct, info);
            case Firefox.PKG -> new Firefox(act, instruct, info);
            case Tor.PKG -> new Tor(act, instruct, info);
            case Signal.PKG -> new Signal(act, instruct, info);
            case Newpipe.PKG -> new Newpipe(act, instruct, info);
            case Tubular.PKG -> new Tubular(act, instruct, info);
            case Github.PKG -> new Github(act, instruct, info);
            case Youtube.PKG -> new Youtube(act, instruct, info);
            case Discord.PKG -> new Discord(act, instruct, info);
            case Outlook.PKG -> new Outlook(act, instruct, info);
            default -> info.hasSend ? new AppSend(act, instruct, info)
                    : new AppCommand(act, instruct, info);
            // Todo: generic AppSearchCommand in a way that handles conflicts with AppSendCommand
        };
    }

    private EmillaCommand singInstance(AssistActivity act, short id, String instruct) {
        if (id >= DEFAULT) {
            if (mCoreCmds[id] == null) return newCore(act, id, instruct);
            mCoreCmds[id].instruct(instruct);
            return mCoreCmds[id];
        }
        int appId = ~id;
        if (mAppCmds[appId] == null) return mAppCmds[appId] = newApp(act, instruct, mAppInfos[appId]);
        mAppCmds[appId].instruct(instruct);
        return mAppCmds[appId];
    }

    private EmillaCommand instance(AssistActivity act, CmdNode node, short id, String instruct) {
        if (id == DUPLICATE) {
            EmillaCommand[] cmds = new EmillaCommand[node.dupes.length];
            int i = -1;
            for (short dupeId : node.dupes) cmds[++i] = singInstance(act, dupeId, instruct);
            return new DuplicateCommand(act, instruct, cmds);
            // Todo: maybe store these in a List if we don't want to call that constructor more than once
        }
        return singInstance(act, id, instruct);
    }

    private EmillaCommand restrainInstance(AssistActivity act, short id, CmdNode node, String instruct) {
        return id < 0 && mAppInfos[~id].basic ? instance(act, node, DEFAULT, instruct)
                : instance(act, node, id, instruct);
    }

    /**
     * If a command starts with something like an app name but is followed by more text, we want to
     * revert to the default command since the the instructions would be useless.
     *
     * @param id is queried on whether it's a non-instruction command.
     * @return `id` if it's an instruction command, {@link EmillaCommand#DEFAULT} otherwise.
     */
    private short restrain(short id) {
        // Todo: we may not want to fall entirely back to the default command in some cases. There's a
        //  good chance you just want to traverse up the tree one step. I.e. a one-word subcommand that
        //  yields to the parent command if more instruction is provided.
        //  Would entail parent pointers for the nodes, and `cmd` of the root being set to the dfltCmd
        return id < 0 && mAppInfos[~id].basic ? DEFAULT : id;
    }

    /**
     * Searches the tree for a mapping of `command`. If one is found, a new instance is created using
     * `act`, stored in {@link this#mCoreCmds}, and returned. Otherwise, the default command instance is
     * returned.
     *
     * @param act is used to generate new command instances when necessary
     */
    public EmillaCommand get(AssistActivity act, String command) {
        // Todo: why is this called twice sometimes? Is it because of rich input stuff?
        if (command.isBlank()) return singInstance(act, DEFAULT, null);

        CmdNode cur = root;
        short curCmd = DEFAULT;
        CmdTokens tokens = Lang.cmdTokens(mRes, command);
        for (String token : tokens) {
            if (cur.map == null) return restrainInstance(act, curCmd, cur, tokens.instruct());
            CmdNode get = cur.map.get(token);
            if (get == null) return restrainInstance(act, curCmd, cur, tokens.instruct());
            cur = get;
            curCmd = get.cmd == DEFAULT ? restrain(curCmd) : get.cmd;
            tokens.nextInstruct();
        }
        return instance(act, cur, curCmd, tokens.instruct());
    }
}
