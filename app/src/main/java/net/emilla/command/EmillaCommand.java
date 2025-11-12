package net.emilla.command;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.InstructyGadget;
import net.emilla.activity.AssistActivity;
import net.emilla.command.app.AppCommand;
import net.emilla.command.app.AppEntry;
import net.emilla.command.app.AppYielder;
import net.emilla.command.core.CoreEntry;
import net.emilla.config.Aliases;
import net.emilla.config.SettingVals;
import net.emilla.lang.Lang;
import net.emilla.ping.PingChannel;
import net.emilla.run.AppGift;
import net.emilla.run.AppSuccess;
import net.emilla.run.BroadcastGift;
import net.emilla.run.DialogRun;
import net.emilla.run.MessageFailure;
import net.emilla.run.PingGift;
import net.emilla.run.TextGift;
import net.emilla.run.TimePickerOffering;
import net.emilla.util.ArrayLoader;
import net.emilla.util.Dialogs;
import net.emilla.util.Patterns;

import java.util.Objects;
import java.util.Set;

public abstract class EmillaCommand {

    public static CommandMap map(
        SharedPreferences prefs,
        Resources res,
        PackageManager pm,
        Iterable<AppEntry> appList
    ) {
        Aliases.reformatCoresIfNecessary(prefs);

        var map = new CommandMap(SettingVals.defaultCommand(prefs));

        for (var coreEntry : CoreEntry.values()) {
            if (!coreEntry.isEnabled(pm, prefs)) {
                continue;
            }

            CommandYielder yielder = coreEntry.yielder();
            map.put(coreEntry.name(res), yielder);

            Set<String> aliases = coreEntry.aliases(prefs, res);
            if (aliases == null) {
                continue;
            }

            for (String alias : aliases) {
                map.put(alias, yielder);
            }
        }

        for (AppEntry app : appList) {
            if (!app.isEnabled(prefs)) {
                continue;
            }

            // todo: edge case where a mapped app is uninstalled during the activity lifecycle
            AppYielder yielder = app.yielder();
            map.put(app.displayName, yielder);

            Set<String> aliases = app.aliases(prefs, res);
            if (aliases == null) {
                continue;
            }

            for (String alias : aliases) {
                map.put(alias, yielder);
            }
        }

        Set<String> customs = SettingVals.customCommands(prefs);
        // Todo: custom commands with preset instructions.
        // Todo: the current string-set approach means it's anyone's guess whether custom aliases
        //  can *successfully* map to one another at mapping time, and it guarantees they can't be
        //  reciprocally used. that's good for now since that'd be infinite recursion, but this
        //  should be borne in mind when creating a more robust custom command system.
        for (String customEntry : customs) {
            String[] split = Patterns.TRIMMING_CSV.split(customEntry);
            int last = split.length - 1;
            for (int i = 0; i < last; ++i) {
                map.putCustom(split[i], split[last]);
            }
        }

        return map;
    }

    private final Params mParams;

    public final String name;
    @StringRes
    public final int summary;
    @StringRes
    public final int manual;
    /// The command's "IME action." This determines the soft keyboard's enter key icon. The options
    /// are GO, SEARCH, SEND, DONE, and NEXT. GO is usually a forward arrow, SEARCH is usually a
    /// magnifying glass, SEND is usually a paper airplane, and DONE is usually a checkmark. NEXT is
    /// the 'tab' function and should be used when the data field is available.
    private final int mImeAction;
    // todo: you should be able to long-click the enter key in the command or data field to
    //  submit the command, using an appropriate action icon.
    // requires changing the input method code directly
    // it's also proven cumbersome to get the key icon to actually update to begin with..

    @Nullable
    private String mInstruction = null;
    private boolean mActive = false;

    @Nullable
    private Gadget[] mGadgets = null;
    @Nullable
    private InstructyGadget[] mInstructyGadgets = null;

    protected EmillaCommand(
        Context ctx,
        Params params,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        mParams = params;

        var res = ctx.getResources();

        this.name = params.name(res);
        this.summary = summary;
        this.manual = manual;
        this.mImeAction = imeAction;
    }

    protected EmillaCommand(Context ctx, CoreEntry coreEntry, int imeAction) {
        this(ctx, coreEntry, coreEntry.summary, coreEntry.manual, imeAction);
    }

    protected EmillaCommand(Context ctx, AppEntry appEntry, int imeAction) {
        this(ctx, appEntry, appEntry.summary(), appEntry.actions.manual(), imeAction);
    }

    /*internal*/ final void instruct(@Nullable String instruction) {
        if (!Objects.equals(mInstruction, instruction)) {
            // we don't assume this is true because input editor bugs may cause onTextChanged() to
            // be called repeatedly for the same text.
            mInstruction = instruction;
            if (mActive) onInstruct(instruction);
        }
    }

    public final void decorate(AssistActivity act, Resources res, boolean setIcon, boolean isDefault) {
        CharSequence title;
        if (isDefault) {
            String sentenceName = mParams.isProperNoun() ? this.name : this.name.toLowerCase();
            title = Lang.colonConcat(res, R.string.command_default, sentenceName);
        } else {
            title = mParams.title(res);
        }
        act.updateTitle(title);
        act.updateDataHint();
        act.setImeAction(mImeAction);
        if (setIcon) {
            act.setSubmitIcon(mParams.icon(act), this instanceof AppCommand);
        }
    }

    public final void load(AssistActivity act) {
        if (mGadgets != null) {
            for (Gadget gadget : mGadgets) {
                gadget.load(act);
            }

            onInstruct(mInstruction);
        }

        mActive = true;
    }

    public /*open*/ void unload(AssistActivity act) {
        if (mGadgets != null) {
            for (Gadget gadget : mGadgets) {
                gadget.unload(act);
            }
        }

        mActive = false;
    }

    protected final void giveGadgets(Gadget... gadgets) {
        int gadgetCount = gadgets.length;

        if (mGadgets == null) {
            mGadgets = gadgets;
        } else {
            mGadgets = ArrayLoader.concat(mGadgets, gadgets);
        }

        var instructyGadgets = new ArrayLoader<InstructyGadget>(gadgetCount, InstructyGadget[]::new);
        for (Gadget gadget : gadgets) {
            if (gadget instanceof InstructyGadget instructyGadget) {
                instructyGadgets.add(instructyGadget);
            }
        }

        if (instructyGadgets.notEmpty()) {
            if (mInstructyGadgets == null) {
                mInstructyGadgets = instructyGadgets.array();
            } else {
                mInstructyGadgets = instructyGadgets.appendedTo(mInstructyGadgets);
            }
        }
    }

    // TODO: make final and handle subcommands in a more centralized manner
    protected /*open*/ void onInstruct(@Nullable String instruction) {
        if (mInstructyGadgets != null) {
            for (InstructyGadget gadget : mInstructyGadgets) {
                gadget.instruct(instruction);
            }
        }
    }

    @Nullable
    protected final String instruction() {
        return mInstruction;
    }

    public final void execute(AssistActivity act) {
        if (mInstruction != null) {
            run(act, mInstruction);
        } else {
            run(act);
        }
    }

    /*======================================================================================*
     * IMPORTANT: One of the following methods should be called at the end of each command. *
     *======================================================================================*/

    /// Simply close the assistant :)
    protected static void succeed(AssistActivity act) {
        act.succeed(Activity::finishAndRemoveTask);
    }

    /// Tells the AssistActivity to close and start the `intent` activity. The succeeding activity
    /// must never be excluded from the recents.
    ///
    /// @param intent is launched after the assistant closes. It's very important that this is
    ///               resolvable, else an ANF exception will occur.
    protected static void appSucceed(AssistActivity act, Intent intent) {
        act.succeed(new AppSuccess(intent));
    }

    protected final void giveText(AssistActivity act, @StringRes int msg) {
        act.give(new TextGift(act, this.name, msg));
    }

    protected final void giveText(AssistActivity act, CharSequence msg) {
        act.give(new TextGift(act, this.name, msg));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    protected static void givePing(AssistActivity act, Notification ping, PingChannel channel) {
        act.give(new PingGift(ping, channel));
    }

    protected static void giveBroadcast(AssistActivity act, Intent intent) {
        act.give(new BroadcastGift(intent));
    }

    protected static void giveApp(AssistActivity act, Intent intent) {
        act.give(new AppGift(intent));
    }

    protected static void offerDialog(AssistActivity act, AlertDialog.Builder builder) {
        act.offer(new DialogRun(builder));
    }

    protected static void offerTimePicker(AssistActivity act, OnTimeSetListener timeSet) {
        act.offer(new TimePickerOffering(timeSet));
    }

    protected static void offerApp(AssistActivity act, Intent intent, boolean newTask) {
        if (newTask) {
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        } else {
            act.suppressBackCancellation();
        }

        act.startActivity(intent);
    }

    protected final void failDialog(
        AssistActivity act,
        @StringRes int msg,
        @StringRes int yesLabel,
        DialogInterface.OnClickListener yesClick
    ) {
        act.fail(new DialogRun(Dialogs.dual(act, this.name, msg, yesLabel, yesClick)));
    }

    protected final void failMessage(AssistActivity act, @StringRes int msg) {
        act.fail(new MessageFailure(act, this.name, msg));
    }

    protected final void failMessage(AssistActivity act, CharSequence msg) {
        act.fail(new MessageFailure(act, this.name, msg));
    }

    /*==========================*
     * End of finisher methods. *
     *==========================*/

    /// Runs the command.
    protected abstract void run(AssistActivity act);
    /// Runs the command with instruction.
    ///
    /// @param instruction is provided after in the command field after the command's name. It's
    /// always space-trimmed and should remain as such.
    protected abstract void run(AssistActivity act, String instruction);

}
