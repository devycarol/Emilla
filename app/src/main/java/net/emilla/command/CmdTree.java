package net.emilla.command;

import static net.emilla.command.EmillaCommand.*;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.app.AppCommand;
import net.emilla.command.app.AppSearch;
import net.emilla.command.app.AppSend;
import net.emilla.command.app.AppSendData;
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bookmark;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.CateCalculate;
import net.emilla.command.core.CateNavigate;
import net.emilla.command.core.CateWeather;
import net.emilla.command.core.Clock;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.Dial;
import net.emilla.command.core.Email;
import net.emilla.command.core.OpenInfo;
import net.emilla.command.core.OpenLaunch;
import net.emilla.command.core.OpenUninstall;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.Settings;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Web;
import net.emilla.lang.Lang;
import net.emilla.settings.SettingVals;
import net.emilla.utils.Apps;

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
    private final AppCommand.AppParams[] mAppParamSets;
    private final AppCommand[] mAppCmds;

    public CmdTree(Resources res, int appCount) {
        mRes = res;
        root.map = new HashMap<>();
        mAppParamSets = new AppCommand.AppParams[appCount];
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
     * @param params is used to generate an AppCommand instance in
     *               {@link this#singInstance(AssistActivity, short, String)}
     * @param idx must be the bitwise NOT of `id`, used to store `params` in the
     *            {@link this#mAppParamSets} array.
     */
    public void putApp(CharSequence label, short id, AppCommand.AppParams params, int idx) {
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
        mAppParamSets[idx] = params;
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
            case NAVIGATE -> new CateNavigate(act, instruct);
            case LAUNCH -> new OpenLaunch(act, instruct);
            case COPY -> new Copy(act, instruct);
            case SHARE -> new Share(act, instruct);
            case SETTINGS -> new Settings(act, instruct);
    //        case NOTE -> new CommandNote(act, instruct);
    //        case TODO -> new CommandTodo(act, instruct);
            case WEB -> new Web(act, instruct);
    //        case FIND -> new CommandFind(act, instruct);
            case CLOCK -> new Clock(act, instruct);
            case ALARM -> new Alarm(act, instruct);
            case TIMER -> new Timer(act, instruct);
            case POMODORO -> new Pomodoro(act, instruct);
            case CALENDAR -> new Calendar(act, instruct);
            case CONTACT -> new Contact(act, instruct);
    //        case NOTIFY -> new CommandNotify(act, instruct);
            case CALCULATE -> new CateCalculate(act, instruct);
            case WEATHER -> new CateWeather(act, instruct);
            case BOOKMARK -> new Bookmark(act, instruct);
            case TORCH -> new Torch(act, instruct);
            case INFO -> new OpenInfo(act, instruct);
            case UNINSTALL -> new OpenUninstall(act, instruct);
            case TOAST -> new Toast(act, instruct);
            default -> null;
        };
        mCoreCmds[id] = cmd;
        return cmd;
    }

    @NonNull
    private static AppCommand newApp(AssistActivity act, AppCommand.AppParams params, String instruct) {
        return switch (params.pkg) {
        case Apps.PKG_AOSP_CONTACTS -> new AppSearch(act, instruct, params, R.string.instruction_contact);
        case Apps.PKG_MARKOR -> params.cls.equals(Apps.CLS_MARKOR_MAIN)
                ? new AppSendData(act, instruct, params, R.string.instruction_text)
            : new AppCommand(act, instruct, params);
        // Markor can have multiple launchers. Only the main one should have the 'send' property.
        case Apps.PKG_FIREFOX -> new AppSearch(act, instruct, params, R.string.instruction_web);
        case Apps.PKG_TOR -> new AppCommand(act, instruct, params);
        // Tor search/send intents are broken.
        case Apps.PKG_SIGNAL -> new AppSendData(act, instruct, params, R.string.instruction_message);
        case Apps.PKG_NEWPIPE,
             Apps.PKG_TUBULAR -> new AppSend(act, instruct, params, R.string.instruction_video);
        case Apps.PKG_GITHUB -> new AppSendData(act, instruct, params, R.string.instruction_issues);
        case Apps.PKG_YOUTUBE -> new AppSearch(act, instruct, params, R.string.instruction_video);
        case Apps.PKG_DISCORD -> new AppSend(act, instruct, params, R.string.instruction_message);
        default -> params.has_send ? new AppSend(act, instruct, params) : new AppCommand(act, instruct, params);
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
        if (mAppCmds[appId] == null) return mAppCmds[appId] = newApp(act, mAppParamSets[appId], instruct);
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
        return id < 0 && mAppParamSets[~id].basic ? instance(act, node, DEFAULT, instruct)
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
        return id < 0 && mAppParamSets[~id].basic ? DEFAULT : id;
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
