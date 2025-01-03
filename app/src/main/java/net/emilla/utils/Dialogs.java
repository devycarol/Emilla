package net.emilla.utils;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.format.DateFormat;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.run.AppSuccess;
import net.emilla.run.ToastFailure;

import java.util.List;

public final class Dialogs {

    public static AlertDialog.Builder base(AssistActivity act, @StringRes int title) {
        return new AlertDialog.Builder(act)
                .setTitle(title)
                .setOnCancelListener(dialog -> act.onCloseDialog(true)); // todo: separate function for the special case that doesn't want this listener
    }

    public static AlertDialog.Builder msg(AssistActivity act, @StringRes int title, @StringRes int msg) {
        return base(act, title).setMessage(msg);
    }

    public static AlertDialog.Builder msg(AssistActivity act, @StringRes int title, String msg) {
        return base(act, title).setMessage(msg);
    }

    private static AlertDialog.Builder dual(AssistActivity act, AlertDialog.Builder builder,
            @StringRes int posId, int negId, DialogInterface.OnClickListener yes) {
        return builder.setPositiveButton(posId, yes)
                .setNegativeButton(negId, (dialog, which) -> act.onCloseDialog(true));
    }

    public static AlertDialog.Builder okCancel(AssistActivity act, @StringRes int title,
            @StringRes int yesId, DialogInterface.OnClickListener yes) {
        return dual(act, base(act, title), yesId, android.R.string.cancel, yes);
    }

    public static AlertDialog.Builder okCancel(AssistActivity act, @StringRes int title,
            DialogInterface.OnClickListener yes) {
        return dual(act, base(act, title), android.R.string.ok, android.R.string.cancel, yes);
    }

    public static AlertDialog.Builder okCancelMsg(AssistActivity act,
            @StringRes int title, @StringRes int msg, @StringRes int yesId,
            DialogInterface.OnClickListener yes) {
        return dual(act, msg(act, title, msg), yesId, android.R.string.cancel, yes);
    }

    public static AlertDialog.Builder okCancelMsg(AssistActivity act, @StringRes int title,
            String msg, @StringRes int yesId,
            DialogInterface.OnClickListener yes) {
        return dual(act, msg(act, title, msg), yesId, android.R.string.cancel, yes);
    }

    public static AlertDialog.Builder okCancelMsg(AssistActivity act,
            @StringRes int title, @StringRes int msg,
            DialogInterface.OnClickListener yes) {
        return dual(act, msg(act, title, msg), android.R.string.ok, android.R.string.cancel, yes);
    }

    public static AlertDialog.Builder yesNoMsg(AssistActivity act,
            @StringRes int title, String msg,
            DialogInterface.OnClickListener yes) {
        return dual(act, msg(act, title, msg), R.string.yes, R.string.no, yes);
    }

    public static TimePickerDialog timePicker(Context ctx, OnTimeSetListener listener) {
        return new TimePickerDialog(ctx, 0, listener, 12, 0, DateFormat.is24HourFormat(ctx));
        // TODO: this isn't respecting the LineageOS system 24-hour setting
        // should there be an option for default time to be noon vs. the current time? noon seems much
        // more reasonable in all cases tbh. infinitely more predictable—who the heck wants to set a
        // timer for right now?!
    }

    public static AlertDialog.Builder withIntents(AlertDialog.Builder builder,
            AssistActivity act, CharSequence[] labels, Intent[] intents) {
        return builder.setItems(labels, (dialog, which) -> act.succeed(new AppSuccess(act, intents[which])));
    }

    public static AlertDialog.Builder withNullableIntents(AlertDialog.Builder builder,
            AssistActivity act, CharSequence[] labels, Intent[] intents,
            String failMsg) {
        return builder.setItems(labels, (dialog, which) -> {
            Intent in = intents[which];
            if (in == null) act.fail(new ToastFailure(act, failMsg));
            else act.succeed(new AppSuccess(act, in));
        });
    }

    private static AlertDialog.Builder withApps(AlertDialog.Builder builder,
            AssistActivity act, PackageManager pm, List<ResolveInfo> appList) {
        return withIntents(builder, act, Apps.labels(appList, pm), Apps.intents(appList));
    }

    public static AlertDialog.Builder appChooser(AssistActivity act, PackageManager pm,
            List<ResolveInfo> appList) {
        // TODO: due to duplicate app labels, it's important to eventually include app icons in this dialog
        // TODO: allow for alpha sort. important for screen readers (it should be on by default in that case)
        //  only reason not to is i kind of like the random order. makes my phone feel less cramped.
        //  on the accessibility topic, being able to speak/type a (nato?) letter would be helpful for everyone.
        //  basically: very accessible search interface
        // a choice between list and grid layout would be cool
        AlertDialog.Builder base = base(act, R.string.dialog_app);
        return withApps(base, act, pm, appList);
    }

    private static AlertDialog.Builder withUninstalls(AlertDialog.Builder builder,
            AssistActivity act, PackageManager pm, List<ResolveInfo> appList) {
        return withNullableIntents(builder, act, Apps.labels(appList, pm), Apps.uninstalls(appList, pm),
                "No settings app found for your device"); // Todo: instead handle at mapping somehow
    }

    public static AlertDialog.Builder appUninstaller(AssistActivity act,
            List<ResolveInfo> appList) {
        AlertDialog.Builder base = base(act, R.string.dialog_app);
        return withUninstalls(base, act, act.getPackageManager(), appList);
    }

    private Dialogs() {}
}
