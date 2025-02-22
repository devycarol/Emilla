package net.emilla.command;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.action.QuickAction;
import net.emilla.command.app.AppCommand;
import net.emilla.command.core.Alarm;
import net.emilla.command.core.Bookmark;
import net.emilla.command.core.Calculate;
import net.emilla.command.core.Calendar;
import net.emilla.command.core.Call;
import net.emilla.command.core.Contact;
import net.emilla.command.core.Copy;
import net.emilla.command.core.CoreCommand;
import net.emilla.command.core.Dial;
import net.emilla.command.core.Email;
import net.emilla.command.core.Info;
import net.emilla.command.core.Launch;
import net.emilla.command.core.Navigate;
import net.emilla.command.core.Pomodoro;
import net.emilla.command.core.Share;
import net.emilla.command.core.Sms;
import net.emilla.command.core.Snippets;
import net.emilla.command.core.Time;
import net.emilla.command.core.Timer;
import net.emilla.command.core.Toast;
import net.emilla.command.core.Torch;
import net.emilla.command.core.Uninstall;
import net.emilla.command.core.Weather;
import net.emilla.command.core.Web;
import net.emilla.run.AppSuccess;
import net.emilla.run.BroadcastGift;
import net.emilla.run.DialogFailure;
import net.emilla.run.DialogOffering;
import net.emilla.run.Failure;
import net.emilla.run.Gift;
import net.emilla.run.MessageFailure;
import net.emilla.run.Offering;
import net.emilla.run.Success;
import net.emilla.run.TimePickerOffering;
import net.emilla.run.ToastGift;
import net.emilla.settings.SettingVals;
import net.emilla.util.Dialogs;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class EmillaCommand {

    private static CoreCommand.Yielder[] coreYielders() {
        return new CoreCommand.Yielder[] {
                Call.yielder(),
                Dial.yielder(),
                Sms.yielder(),
                Email.yielder(),
                Navigate.yielder(),
                Launch.yielder(),
                Copy.yielder(),
                Snippets.yielder(),
                Share.yielder(),
//                Setting.yielder(),
//                Note.yielder(),
//                Todo.yielder(),
                Web.yielder(),
//                Find.yielder(),
                Time.yielder(),
                Alarm.yielder(),
                Timer.yielder(),
                Pomodoro.yielder(),
                Calendar.yielder(),
                Contact.yielder(),
//                Notify.yielder(),
                Calculate.yielder(),
                Weather.yielder(),
                Bookmark.yielder(),
                Torch.yielder(),
                Info.yielder(),
                Uninstall.yielder(),
                Toast.yielder(),
        };
    }

    public static CommandMap map(
        SharedPreferences prefs,
        Resources res,
        PackageManager pm,
        List<ResolveInfo> appList
    ) {
        CoreCommand.Yielder[] coreYielders = coreYielders();

        var map = new CommandMap(SettingVals.defaultCommand(prefs, coreYielders));

        for (CoreCommand.Yielder yielder : coreYielders) {
            map.put(yielder.name(res), yielder);
            for (String alias : yielder.aliases(prefs, res)) map.put(alias, yielder);
        }

        for (ResolveInfo ri : appList) {
            // todo: edge case where a mapped app is uninstalled during the activity lifecycle
            var yielder = new AppCommand.Yielder(ri.activityInfo, pm);

            map.put(yielder.name(), yielder);
            Set<String> aliases = yielder.aliases(prefs, res);
            if (aliases == null) continue; // Todo: alias config for all apps.
            for (String alias : aliases) map.put(alias, yielder);
        }

        Set<String> customs = SettingVals.customCommands(prefs);
        // Todo: custom commands with preset instructions.
        // Todo: the current string-set approach means it's anyone's guess whether custom aliases
        //  can *successfully* map to one another at mapping time, and it guarantees they can't be
        //  reciprocally used. that's good for now since that'd be infinite recursion, but this
        //  should be borne in mind when creating a more robust custom command system.
        for (String customEntry : customs) {
            var split = customEntry.split(", *");
            int lastIdx = split.length - 1;
            for (int i = 0; i < lastIdx; ++i) map.putCustom(split[i], split[lastIdx]);
        }

        return map;
    }

    protected final AssistActivity activity;
    protected final Resources resources;
    private final Params mParams;

    private String mInstruction;
    private boolean mInitialized;
    private Drawable mIcon;

    protected EmillaCommand(AssistActivity act, Params params) {
        this.activity = act;
        this.resources = act.getResources();
        mParams = params;
    }

    final EmillaCommand instruct(String instruction) {
        if (!Objects.equals(mInstruction, instruction)) {
            // we don't assume this is true because input editor bugs may cause onTextChanged() to
            // be called repeatedly for the same text.
            mInstruction = instruction;
            if (mInitialized) onInstruct(instruction);
        }

        return this;
    }

    protected final void setInstruction(String instruction) {
        mInstruction = instruction;
    }

    @Deprecated
    protected final void instructAppend(String data) {
        if (mInstruction == null) mInstruction = data;
        else mInstruction += '\n' + data;
    }

    public final void decorate(boolean setIcon) {
        activity.updateTitle(title());
        activity.updateDataHint();
        activity.setImeAction(imeAction());
        if (setIcon) activity.setSubmitIcon(icon(), mParams.usesAppIcon());
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
    protected void onInit() {}

    @CallSuper
    protected void onInstruct(String instruction) {}

    @CallSuper
    protected void onClean() {}

    protected final String instruction() {
        return mInstruction;
    }

    protected final String str(@StringRes int id) {
        return resources.getString(id);
    }

    protected final String str(@StringRes int id, Object ... formatArgs) {
        return resources.getString(id, formatArgs);
    }

    protected final String quantityString(@PluralsRes int id, int quantity) {
        return resources.getQuantityString(id, quantity, quantity);
    }

    protected final String[] stringArray(@ArrayRes int id) {
        return resources.getStringArray(id);
    }

    protected final SharedPreferences prefs() {
        return activity.prefs();
    }

    protected final PackageManager pm() {
        return activity.getPackageManager();
    }

    protected final ContentResolver contentResolver() {
        return activity.getContentResolver();
    }

    public final void execute() {
        if (mInstruction == null) run();
        else run(mInstruction);
    }

    /**
     * Todo: these toast messages should generally be replaced with widget dialogs (which would have
     *  their own "more info please" chime). Excessive toasting is disruptive (the messages cover
     *  the keyboard and are opaque in many ROMs)
     *
     * @param text is shown as a toast notification at the bottom of the screen. Don't use
     *             hard-coded text.
     */
    protected final void toast(CharSequence text) {
        activity.toast(text);
    }

    protected final void chime(byte id) {
        activity.chime(id);
    }

    protected final void giveAction(QuickAction action) {
        activity.addAction(action);
    }

    protected final void reshowField(@IdRes int id) {
        activity.reshowField(id);
    }

    protected final void hideField(@IdRes int id) {
        activity.hideField(id);
    }

    protected final void removeAction(@IdRes int id) {
        activity.removeAction(id);
    }

    /*======================================================================================*
     * IMPORTANT: One of the following methods should be called at the end of each command. *
     *======================================================================================*/

    /**
     * Simply close the assistant :)
     */
    protected final void succeed() {
        activity.succeed(activity::finishAndRemoveTask);
    }

    /**
     * Completes the work that the user wants from the assistant and closes it. Typically entails
     * opening another app window, handing work to another program.
     *
     * @param success finishes the work of the assistant.
     */
    protected final void succeed(Success success) {
        activity.succeed(success);
    }

    /**
     * Tells the AssistActivity to close and start the `intent` activity. The succeeding activity must
     * never be excluded from the recents.
     *
     * @param intent is launched after the assistant closes. It's very important that this is
     *               resolvable, else an ANF exception will occur.
     */
    protected final void appSucceed(Intent intent) {
        succeed(new AppSuccess(activity, intent));
    }

    /**
     * Gives the user a gadget to play with. The assistant is done with its work for now and the
     * user can use the gadget for whatever they need it for.
     *
     * @param gift gadget for the user.
     */
    protected final void give(Gift gift) {
        activity.give(gift);
    }

    protected final void giveBroadcast(Intent intent) {
        give(new BroadcastGift(activity, intent));
    }

    /**
     * Todo: these toast messages should generally be replaced with widget dialogs (which would have
     *  their own "here you go" chime). Text displayed in toasts can't be copied, and excessive
     *  toasting is disruptive (the messages cover the keyboard and are opaque in many ROMs)
     *
     * @param text is shown as a toast notification at the bottom of the screen. Don't hard-code text.
     * @param longToast whether to use Toast.LENGTH_LONG. Use this sparingly, for reasons above.
     */
    @Deprecated
    protected final void giveText(CharSequence text, boolean longToast) {
        give(new ToastGift(activity, text, longToast));
    }

    /**
     * Offers the user a tool to complete their command. Successful use of the tool should lead to a
     * prompt 'success', whereas canceled use should lead to a full reset of the command and UI to
     * their pre-offer state.
     *
     * @param offering tool for the user.
     */
    protected final void offer(Offering offering) {
        activity.offer(offering);
    }

    protected final void offerDialog(AlertDialog.Builder builder) {
        offer(new DialogOffering(activity, builder));
    }

    protected final void offerTimePicker(OnTimeSetListener timeSet) {
        offer(new TimePickerOffering(activity, timeSet));
    }

    protected final void offerApp(Intent intent, boolean newTask) {
        if (newTask) intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        else activity.suppressBackCancellation();
        activity.startActivity(intent);
    }

    /**
     * Stops the command in its tracks because something has gone wrong. Offers the user a tool to
     * help fix the problem.
     *
     * @param failure tool for the user to resolve the issue.
     */
    protected final void fail(Failure failure) {
        activity.fail(failure);
    }

    protected final void failDialog(
        @StringRes int msg,
        @StringRes int yesLabel,
        DialogInterface.OnClickListener yesClick
    ) {
        fail(new DialogFailure(activity, Dialogs.dual(activity, name(), msg, yesLabel, yesClick)));
    }

    protected final void failMessage(@StringRes int msg) {
        fail(new MessageFailure(activity, name(), msg));
    }

    protected final void failMessage(CharSequence msg) {
        fail(new MessageFailure(activity, name(), msg));
    }

    /*==========================*
     * End of finisher methods. *
     *==========================*/

    public boolean usesData() {
        return false;
    }

    public final CharSequence name() {
        return mParams.name(resources);
    }

    @Deprecated
    protected abstract String dupeLabel(); // Todo: replace with icons

    protected final CharSequence sentenceName() {
        return mParams.shouldLowercase() ? name().toString().toLowerCase() : name();
    }

    protected final CharSequence title() {
        return mParams.title(resources);
    }

    protected final Drawable icon() {
        return mIcon == null ? mIcon = mParams.icon(activity) : mIcon;
    }

    @StringRes
    public final int summary() {
        return mParams.summary();
    }

    @StringRes
    public final int manual() {
        return mParams.manual();
    }

    protected final int imeAction() {
        return mParams.imeAction();
    }

    protected interface Params {

        /**
         * The command's name in Title Case.
         *
         * @param res can be used to retrieve the name from string resources.
         * @return the name of the command.
         */
        CharSequence name(Resources res);
        /**
         * Whether the command should be lowercased mid-sentence.
         *
         * @return true if the command is a common noun, false if the command is a proper noun.
         */
        boolean shouldLowercase();
        /**
         * The command's title as it should appear in the assistant's action-bar. Usually, this
         * should be the command name followed by a brief description of what it takes as input.
         *
         * @param res can be used to retrieve the title from string resources.
         * @return the command's slightly detailed title.
         */
        CharSequence title(Resources res);

        /**
         * The command's icon for the submit button
         *
         * @param ctx can be used to retrieve the icon from drawable resources.
         * @return the command's icon drawable.
         */
        Drawable icon(Context ctx);

        /**
         * Whether the command's icon is an app icon.
         *
         * @return true if the command uses an app's icon, false if it just uses clip art.
         */
        boolean usesAppIcon();

        /**
         * The command's "IME action." This determines which icon is used for the soft keyboard's
         * enter key. The options are GO, SEARCH, SEND, DONE, and NEXT. GO is usually a forward
         * arrow, SEARCH is usually a magnifying glass, SEND is usually a paper airplane, and DONE
         * is usually a checkmark. NEXT is the 'tab' function and should be used when the data field
         * is available.
         *
         * @return the command's IME action ID.
         */
        int imeAction();
        // todo: you should be able to long-click the enter key in the command or data field to submit
        //  the command, using the action icon of one of the below.
        // requires changing the input method code directly
        // it's also proven cumbersome to get the key icon to actually update to begin with..

        /**
         * A brief description of what the command does
         *
         * @return the resource ID for the command's summary
         */
        @StringRes
        int summary();

        /**
         * The command's manual page, used in the 'help' button dialog. This is a detailed
         * description of what the command does and how to use it
         *
         * @return the resource ID for the command's manual page
         */
        @StringRes
        int manual();
    }

    protected abstract void run();
    /**
     * @param instruction is provided after in the command field after the command's name. It's always
     *                    space-trimmed should remain as such.
     */
    protected abstract void run(@NonNull String instruction);
}
