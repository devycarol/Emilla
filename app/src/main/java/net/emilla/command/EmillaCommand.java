package net.emilla.command;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.action.QuickAction;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.AppEntry;
import net.emilla.apps.AppList;
import net.emilla.chime.Chime;
import net.emilla.command.app.AppCommand;
import net.emilla.command.core.CoreCommand;
import net.emilla.command.core.CoreEntry;
import net.emilla.config.SettingVals;
import net.emilla.ping.PingChannel;
import net.emilla.run.AppGift;
import net.emilla.run.AppSuccess;
import net.emilla.run.BroadcastGift;
import net.emilla.run.CommandRun;
import net.emilla.run.DialogRun;
import net.emilla.run.MessageFailure;
import net.emilla.run.PingGift;
import net.emilla.run.TextGift;
import net.emilla.run.TimePickerOffering;
import net.emilla.util.Dialogs;

import java.util.Objects;
import java.util.Set;

public abstract class EmillaCommand {

    public static CommandMap map(
        SharedPreferences prefs,
        Resources res,
        PackageManager pm,
        AppList appList
    ) {
        CoreEntry[] coreEntries = CoreEntry.values();

        int coreCount = coreEntries.length;
        var coreYielders = new CoreCommand.Yielder[coreCount];

        for (int i = 0; i < coreCount; ++i) {
            coreYielders[i] = coreEntries[i].yielder();
        }

        var map = new CommandMap(SettingVals.defaultCommand(prefs));

        for (CoreCommand.Yielder yielder : coreYielders) {
            if (yielder == null) continue;
            // Todo: no yielder should be null once all commands are implemented

            if (yielder.enabled(pm, prefs)) {
                map.put(yielder.name(res), yielder);

                Set<String> aliases = yielder.aliases(prefs, res);
                if (aliases == null) continue;
                for (String alias : aliases) map.put(alias, yielder);
            }
        }

        for (AppEntry app : appList) {
            if (app.commandEnabled(prefs)) {
                // todo: edge case where a mapped app is uninstalled during the activity lifecycle
                var yielder = new AppCommand.Yielder(app);
                map.put(yielder.name(), yielder);

                Set<String> aliases = yielder.aliases(prefs, res);
                if (aliases == null) continue;
                for (String alias : aliases) map.put(alias, yielder);
            }
        }

        Set<String> customs = SettingVals.customCommands(prefs);
        // Todo: custom commands with preset instructions.
        // Todo: the current string-set approach means it's anyone's guess whether custom aliases
        //  can *successfully* map to one another at mapping time, and it guarantees they can't be
        //  reciprocally used. that's good for now since that'd be infinite recursion, but this
        //  should be borne in mind when creating a more robust custom command system.
        for (String customEntry : customs) {
            String[] split = customEntry.split(", *");
            int lastIdx = split.length - 1;
            for (int i = 0; i < lastIdx; ++i) {
                map.putCustom(split[i], split[lastIdx]);
            }
        }

        return map;
    }

    protected final AssistActivity activity;
    protected final Resources resources;
    private final Params params;

    @StringRes
    public final int summary;
    @StringRes
    public final int manual;
    /// The command's "IME action." This determines the soft keyboard's enter key icon. The options
    /// are GO, SEARCH, SEND, DONE, and NEXT. GO is usually a forward arrow, SEARCH is usually a
    /// magnifying glass, SEND is usually a paper airplane, and DONE is usually a checkmark. NEXT is
    /// the 'tab' function and should be used when the data field is available.
    protected final int imeAction;
    // todo: you should be able to long-click the enter key in the command or data field to
    //  submit the command, using an appropriate action icon.
    // requires changing the input method code directly
    // it's also proven cumbersome to get the key icon to actually update to begin with..

    @Nullable
    private String mInstruction = null;
    private boolean mInitialized = false;
    private Drawable mIcon = null;

    protected EmillaCommand(
        AssistActivity act,
        Params params,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        this.activity = act;
        this.resources = act.getResources();

        this.params = params;
        this.summary = summary;
        this.manual = manual;
        this.imeAction = imeAction;
    }

    protected EmillaCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        this(act, coreEntry.params(), coreEntry.summary, coreEntry.manual, imeAction);
    }

    /*internal*/ final EmillaCommand instruct(@Nullable String instruction) {
        if (!Objects.equals(mInstruction, instruction)) {
            // we don't assume this is true because input editor bugs may cause onTextChanged() to
            // be called repeatedly for the same text.
            mInstruction = instruction;
            if (mInitialized) onInstruct(instruction);
        }

        return this;
    }

    protected final void setInstruction(@Nullable String instruction) {
        mInstruction = instruction;
    }

    @Deprecated
    protected final void instructAppend(String data) {
        if (mInstruction == null) {
            mInstruction = data;
        } else {
            mInstruction += '\n' + data;
        }
    }

    public final void decorate(boolean setIcon) {
        this.activity.updateTitle(title());
        this.activity.updateDataHint();
        this.activity.setImeAction(this.imeAction);
        if (setIcon) this.activity.setSubmitIcon(icon(), usesAppIcon());
    }

    public final void init() {
        onInit();
        mInitialized = true;
        onInstruct(mInstruction);
    }

    public final void clean() {
        onClean();
        mInitialized = false;
    }

    @CallSuper
    protected /*open*/ void onInit() {}

    @CallSuper
    protected /*open*/ void onInstruct(@Nullable String instruction) {}

    @CallSuper
    protected /*open*/ void onClean() {}

    @Nullable
    protected final String instruction() {
        return mInstruction;
    }

    protected final String str(@StringRes int id) {
        return this.resources.getString(id);
    }

    protected final String str(@StringRes int id, Object ... formatArgs) {
        return this.resources.getString(id, formatArgs);
    }

    protected final String quantityString(@PluralsRes int id, int quantity) {
        return this.resources.getQuantityString(id, quantity, quantity);
    }

    protected final String[] stringArray(@ArrayRes int id) {
        return this.resources.getStringArray(id);
    }

    protected final SharedPreferences prefs() {
        return this.activity.prefs();
    }

    protected final PackageManager pm() {
        return this.activity.getPackageManager();
    }

    protected final ContentResolver contentResolver() {
        return this.activity.getContentResolver();
    }

    public final void execute() {
        if (mInstruction != null) {
            run(mInstruction);
        } else {
            run();
        }
    }

    /// Show a little message.
    ///
    /// @param text is shown as a toast notification at the bottom of the screen. Don't hard-code
    /// text.
    protected final void toast(CharSequence text) {
        this.activity.toast(text);
    }

    protected final void chime(Chime chime) {
        this.activity.chime(chime);
    }

    protected final void giveAction(QuickAction action) {
        this.activity.addAction(action);
    }

    protected final void reshowField(@IdRes int id) {
        this.activity.reshowField(id);
    }

    protected final void hideField(@IdRes int id) {
        this.activity.hideField(id);
    }

    protected final void removeAction(@IdRes int id) {
        this.activity.removeAction(id);
    }

    /*======================================================================================*
     * IMPORTANT: One of the following methods should be called at the end of each command. *
     *======================================================================================*/

    /// Simply close the assistant :)
    protected final void succeed() {
        this.activity.succeed(Activity::finishAndRemoveTask);
    }

    /// Completes the work that the user wants from the assistant and closes it. Typically entails
    /// opening another app window, handing work to another program.
    ///
    /// @param success finishes the work of the assistant.
    protected final void succeed(CommandRun success) {
        this.activity.succeed(success);
    }

    /// Tells the AssistActivity to close and start the `intent` activity. The succeeding activity
    /// must never be excluded from the recents.
    ///
    /// @param intent is launched after the assistant closes. It's very important that this is
    /// resolvable, else an ANF exception will occur.
    protected final void appSucceed(Intent intent) {
        succeed(new AppSuccess(intent));
    }

    /// Gives the user a gadget to play with. The assistant is done with its work for now and the
    /// user can use the gadget for whatever they need it for.
    ///
    /// @param gift gadget for the user.
    protected final void give(CommandRun gift) {
        this.activity.give(gift);
    }

    protected final void giveText(@StringRes int msg) {
        give(new TextGift(this.activity, name(), msg));
    }

    protected final void giveText(CharSequence msg) {
        give(new TextGift(this.activity, name(), msg));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    protected final void givePing(Notification ping, PingChannel channel) {
        give(new PingGift(ping, channel));
    }

    protected final void giveBroadcast(Intent intent) {
        give(new BroadcastGift(intent));
    }

    protected final void giveApp(Intent intent) {
        give(new AppGift(intent));
    }

    /// Offers the user a tool to complete their command. Successful use of the tool should lead to
    /// a prompt 'success', whereas canceled use should lead to a full reset of the command and UI
    /// to their pre-offer state.
    ///
    /// @param offering tool for the user.
    protected final void offer(CommandRun offering) {
        this.activity.offer(offering);
    }

    protected final void offerDialog(AlertDialog.Builder builder) {
        offer(new DialogRun(builder));
    }

    protected final void offerTimePicker(OnTimeSetListener timeSet) {
        offer(new TimePickerOffering(timeSet));
    }

    protected final void offerApp(Intent intent, boolean newTask) {
        if (newTask) {
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        } else {
            this.activity.suppressBackCancellation();
        }

        this.activity.startActivity(intent);
    }

    /// Stops the command in its tracks because something has gone wrong. Offers the user a tool to
    /// help fix the problem.
    ///
    /// @param failure tool for the user to resolve the issue.
    protected final void fail(CommandRun failure) {
        this.activity.fail(failure);
    }

    protected final void failDialog(
        @StringRes int msg,
        @StringRes int yesLabel,
        DialogInterface.OnClickListener yesClick
    ) {
        fail(new DialogRun(Dialogs.dual(this.activity, name(), msg, yesLabel, yesClick)));
    }

    protected final void failMessage(@StringRes int msg) {
        fail(new MessageFailure(this.activity, name(), msg));
    }

    protected final void failMessage(CharSequence msg) {
        fail(new MessageFailure(this.activity, name(), msg));
    }

    /*==========================*
     * End of finisher methods. *
     *==========================*/

    public /*open*/ boolean usesData() {
        return false;
    }

    public final String name() {
        return params.name(this.resources);
    }

    @Deprecated
    protected abstract String dupeLabel(); // Todo: replace with icons

    protected final CharSequence sentenceName() {
        return shouldLowercase() ? name().toLowerCase() : name();
    }

    /// Whether the command should be lowercased mid-sentence.
    ///
    /// @return true if the command is a common noun, false if the command is a proper noun.
    protected abstract boolean shouldLowercase();

    protected final CharSequence title() {
        return params.title(this.resources);
    }

    protected final Drawable icon() {
        return mIcon == null ? mIcon = params.icon(this.activity) : mIcon;
    }

    /// Whether the command's icon is an app icon.
    ///
    /// @return true if the command uses an app's icon, false if it just uses clip art.
    protected abstract boolean usesAppIcon();

    /// Runs the command.
    protected abstract void run();
    /// Runs the command with instruction.
    /// @param instruction is provided after in the command field after the command's name. It's
    /// always space-trimmed and should remain as such.
    protected abstract void run(String instruction);
}
