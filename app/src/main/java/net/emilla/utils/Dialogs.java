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
import net.emilla.run.MessageFailure;

import java.util.List;

public final class Dialogs {

    public static AlertDialog.Builder base(AssistActivity act, @StringRes int title) {
        return new AlertDialog.Builder(act)
                .setTitle(title)
                .setOnCancelListener(dialog -> act.onCloseDialog()); // Todo: don't require this
    }

    public static AlertDialog.Builder base(AssistActivity act, @StringRes int title,
            @StringRes int msg) {
        return base(act, title).setMessage(msg);
    }

    public static AlertDialog.Builder baseCancel(AssistActivity act, @StringRes int title,
            @StringRes int msg) {
        return baseCancel(act, title, msg, android.R.string.ok);
    }

    public static AlertDialog.Builder baseCancel(AssistActivity act, @StringRes int title,
            @StringRes int msg, @StringRes int negLabel) {
        return base(act, title, msg).setNegativeButton(negLabel,
                (dialog, which) -> act.onCloseDialog());
    }

    public static AlertDialog.Builder dual(AssistActivity act, @StringRes int title,
            @StringRes int msg, @StringRes int posLabel, DialogInterface.OnClickListener yesClick) {
        return dual(act, title, msg, posLabel, android.R.string.cancel, yesClick);
    }

    public static AlertDialog.Builder dual(AssistActivity act, @StringRes int title,
            @StringRes int msg, @StringRes int posLabel, @StringRes int negLabel,
            DialogInterface.OnClickListener yesClick) {
        return baseCancel(act, title, msg, negLabel).setPositiveButton(posLabel, yesClick);
    }

    public static AlertDialog.Builder yesNo(AssistActivity act, @StringRes int title,
            @StringRes int msg, DialogInterface.OnClickListener yes) {
        return dual(act, title, msg, R.string.yes, R.string.no, yes);
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
        CharSequence[] labels = Apps.labels(appList, pm);
        Intent[] intents = Apps.uninstalls(appList, pm);
        return builder.setItems(labels, (dialog, which) -> {
            if (intents[which] == null) act.fail(new MessageFailure(act, R.string.command_uninstall,
                    R.string.error_cant_uninstall));
            // Todo: instead handle at mapping somehow
            else act.succeed(new AppSuccess(act, intents[which]));
        });
    }

    public static AlertDialog.Builder appUninstaller(AssistActivity act,
            List<ResolveInfo> appList) {
        AlertDialog.Builder base = base(act, R.string.dialog_app);
        return withUninstalls(base, act, act.getPackageManager(), appList);
    }

    private Dialogs() {}
}
