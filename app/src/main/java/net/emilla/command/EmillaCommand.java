package net.emilla.command;

import static net.emilla.chime.Chimer.RESUME;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.QuickAction;
import net.emilla.command.app.AppCommand.AppInfo;
import net.emilla.run.AppSuccess;
import net.emilla.run.DialogOffering;
import net.emilla.run.Failure;
import net.emilla.run.Gift;
import net.emilla.run.Offering;
import net.emilla.run.Success;
import net.emilla.run.TimePickerOffering;
import net.emilla.run.ToastGift;
import net.emilla.settings.Aliases;

import java.util.List;
import java.util.Set;

public abstract class EmillaCommand {

    public static final short
            DEFAULT = 0,
            CALL = 1,
            DIAL = 2,
            SMS = 3,
            EMAIL = 4,
            NAVIGATE = 5,
            LAUNCH = 6,
            COPY = 7,
            SHARE = 8,
            SETTINGS = 9,
//            NOTE = ,
//            TODO = ,
            WEB = 10,
//            FIND = ,
            TIME = 11,
            ALARM = 12,
            TIMER = 13,
            POMODORO = 14,
            CALENDAR = 15,
            CONTACT = 16,
//            NOTIFY = ,
            CALCULATE = 17,
            WEATHER = 18,
            BOOKMARK = 19,
            TORCH = 20,
            INFO = 21,
            UNINSTALL = 22,
            TOAST = 23,
            DUPLICATE = 24;

    private static final int[] NAMES = {
            R.string.command_call,
            R.string.command_dial,
            R.string.command_sms,
            R.string.command_email,
            R.string.command_navigate,
            R.string.command_launch,
            R.string.command_copy,
            R.string.command_share,
            R.string.command_settings,
        //    R.string.command_note,
        //    R.string.command_todo,
            R.string.command_web,
        //    R.string.command_find,
            R.string.command_time,
            R.string.command_alarm,
            R.string.command_timer,
            R.string.command_pomodoro,
            R.string.command_calendar,
            R.string.command_contact,
        //    R.string.command_notify,
            R.string.command_calculate,
            R.string.command_weather,
            R.string.command_bookmark,
            R.string.command_torch,
            R.string.command_info,
            R.string.command_uninstall,
            R.string.command_toast
    };

    public static CmdTree tree(SharedPreferences prefs, Resources res, PackageManager pm,
            List<ResolveInfo> appList) {
        // todo: configurable aliasing
        // todo: edge case where a mapped app is uninstalled during the activity lifecycle
        CmdTree cmdTree = new CmdTree(res, appList.size());
        short i = 0;
        while (i < DUPLICATE - 1) {
            String lcName = res.getString(NAMES[i]).toLowerCase();
            Set<String> aliases = Aliases.set(prefs, res, i);
            cmdTree.putSingle(lcName, ++i);
            for (String alias : aliases) cmdTree.put(alias, i);
            // Todo: have separate set for multi-word aliases and use putSingle for the rest
        }
        i = 0;
        for (ResolveInfo ri : appList) {
            ActivityInfo actInfo = ri.activityInfo;
            CharSequence label = actInfo.loadLabel(pm);
            // TODO: there's the biggest performance bottleneck I've found thus far. Look into how the
            //  launcher caches labels for ideas on how to improve the performance of this critical
            //  onCreate task. That is, if they do to begin with..
            AppInfo info = new AppInfo(actInfo, pm, label);
            cmdTree.putApp(label, --i, info, ~i);
            Set<String> aliases = Aliases.appSet(prefs, res, info);
            if (aliases == null) continue;
            for (String alias : aliases) cmdTree.put(alias, i);
            // No need to pass app info again for aliases
            // Todo: have separate set for multi-word aliases and use putSingle for the rest
        }
        return cmdTree;
    }

    protected final AssistActivity activity;
    protected final Resources resources;
    protected String instruction;
    private final Params mParams;
    private Drawable mIcon;

    protected EmillaCommand(AssistActivity act, String instruct, Params params) {
        this.activity = act;
        this.resources = act.getResources();
        this.instruction = instruct;
        mParams = params;
    }

    @CallSuper
    public void init(boolean updateTitle) {
        baseInit(updateTitle);
        activity.setSubmitIcon(icon(), mParams.usesAppIcon());
    }

    public final void baseInit(boolean updateTitle) {
        if (updateTitle) activity.updateTitle(title());
        activity.updateDataHint();
        activity.setImeAction(imeAction());
    }

    @CallSuper
    public void clean() {}

    protected final void instruct(String instruction) {
        this.instruction = instruction;
    }

    protected final void instructAppend(String data) {
        if (instruction == null) instruction = data;
        else instruction += '\n' + data;
    }

    protected String string(@StringRes int id) {
        return resources.getString(id);
    }

    protected String string(@StringRes int id, Object ... formatArgs) {
        return resources.getString(id, formatArgs);
    }

    protected String quantityString(@PluralsRes int id, int quantity) {
        return resources.getQuantityString(id, quantity, quantity);
    }

    protected String[] stringArray(@ArrayRes int id) {
        return resources.getStringArray(id);
    }

    protected PackageManager pm() {
        return activity.getPackageManager();
    }

    public void execute() {
        if (instruction == null) run();
        else run(instruction);
    }

    /**
     * Todo: these toast messages should generally be replaced with widget dialogs (which would have
     *  their own "more info please" chime). Excessive toasting is disruptive (the messages cover
     *  the keyboard and are opaque in many ROMs)
     *
     * @param text is shown as a toast notification at the bottom of the screen. Don't use
     *             hard-coded text.
     */
    protected void toast(CharSequence text) {
        activity.toast(text);
    }

    protected void chime(byte id) {
        activity.chime(id);
    }

    protected void giveAction(QuickAction action) {
        activity.addAction(action);
    }

    protected void reshowField(@IdRes int id) {
        activity.reshowField(id);
    }

    protected void hideField(@IdRes int id) {
        activity.hideField(id);
    }

    protected void removeAction(@IdRes int id) {
        activity.removeAction(id);
    }

    protected void resume() {
        chime(RESUME);
    }

    /*======================================================================================*
     * IMPORTANT: One of the following methods should be called at the end of each command. *
     *======================================================================================*/

    /**
     * Simply close the assistant :)
     */
    protected void succeed() {
        activity.succeed(() -> {});
    }

    /**
     * Completes the work that the user wants from the assistant and closes it. Typically entails
     * opening another app window, handing work to another program.
     *
     * @param success finishes the work of the assistant.
     */
    protected void succeed(Success success) {
        activity.succeed(success);
    }

    /**
     * Tells the AssistActivity to close and start the `intent` activity. The succeeding activity must
     * never be excluded from the recents.
     *
     * @param intent is launched after the assistant closes. It's very important that this is
     *               resolvable, else an ANF exception will occur.
     */
    protected void appSucceed(Intent intent) {
        succeed(new AppSuccess(activity, intent));
    }

    /**
     * Gives the user a gadget to play with. The assistant is done with its work for now and the
     * user can use the gadget for whatever they need it for.
     *
     * @param gift gadget for the user.
     */
    protected void give(Gift gift) {
        activity.give(gift);
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
    protected void giveText(CharSequence text, boolean longToast) {
        give(new ToastGift(activity, text, longToast));
    }

    /**
     * Offers the user a tool to complete their command. Successful use of the tool should lead to a
     * prompt 'success', whereas canceled use should lead to a full reset of the command and UI to
     * their pre-offer state.
     *
     * @param offering tool for the user.
     */
    protected void offer(Offering offering) {
        activity.offer(offering);
    }

    protected void offerDialog(AlertDialog.Builder builder) {
        offer(new DialogOffering(activity, builder));
    }

    protected void offerTimePicker(OnTimeSetListener timeSet) {
        offer(new TimePickerOffering(activity, timeSet));
    }

    /**
     * Stops the command in its tracks because something has gone wrong. Offers the user a tool to
     * help fix the problem.
     *
     * @param failure tool for the user to resolve the issue.
     */
    protected void fail(Failure failure) {
        activity.fail(failure);
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
    public int summary() {
        return mParams.summary();
    }

    @StringRes
    public int manual() {
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
    protected abstract void run(String instruction);
}
