package net.emilla.utils;

import static android.app.AlertDialog.THEME_HOLO_DARK;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.format.DateFormat;

import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

import java.util.List;

public class Dialogs {
public static AlertDialog.Builder base(final AssistActivity act, @StringRes final int titleId) {
    return new AlertDialog.Builder(act, THEME_HOLO_DARK) // TODO: better colorway
            .setTitle(titleId)
            .setOnCancelListener(dialog -> act.onCloseDialog(true)); // todo: separate function for the special case that doesn't want this listener
}

public static AlertDialog.Builder msg(final AssistActivity act,
        @StringRes final int titleId, @StringRes final int messageId) {
    return base(act, titleId).setMessage(messageId);
}
public static AlertDialog.Builder msg(final AssistActivity act,
        @StringRes final int titleId, final CharSequence message) {
    return base(act, titleId).setMessage(message);
}

private static AlertDialog.Builder dual(final AssistActivity act, final AlertDialog.Builder builder,
        @StringRes final int posId, final int negId, final DialogInterface.OnClickListener yes) {
    return builder.setPositiveButton(posId, yes)
            .setNegativeButton(negId, (dialog, which) -> act.onCloseDialog(true));
}

public static AlertDialog.Builder okCancel(final AssistActivity act, @StringRes final int titleId,
        @StringRes final int yesId, final DialogInterface.OnClickListener yes) {
    return dual(act, base(act, titleId), yesId, android.R.string.cancel, yes);
}

public static AlertDialog.Builder okCancel(final AssistActivity act, @StringRes final int titleId,
        final DialogInterface.OnClickListener yes) {
    return dual(act, base(act, titleId), android.R.string.ok, android.R.string.cancel, yes);
}

public static AlertDialog.Builder okCancelMsg(final AssistActivity act,
        @StringRes final int titleId, @StringRes final int messageId, @StringRes final int yesId,
        final DialogInterface.OnClickListener yes) {
    return dual(act, msg(act, titleId, messageId), yesId, android.R.string.cancel, yes);
}

public static AlertDialog.Builder okCancelMsg(final AssistActivity act, @StringRes final int titleId,
        final CharSequence message, @StringRes final int yesId,
        final DialogInterface.OnClickListener yes) {
    return dual(act, msg(act, titleId, message), yesId, android.R.string.cancel, yes);
}

public static AlertDialog.Builder okCancelMsg(final AssistActivity act,
        @StringRes final int titleId, @StringRes final int messageId,
        final DialogInterface.OnClickListener yes) {
    return dual(act, msg(act, titleId, messageId), android.R.string.ok, android.R.string.cancel, yes);
}

public static AlertDialog.Builder yesNoMsg(final AssistActivity act,
        @StringRes final int titleId, final CharSequence message,
        final DialogInterface.OnClickListener yes) {
    return dual(act, msg(act, titleId, message), R.string.yes, R.string.no, yes);
}

public static TimePickerDialog timePicker(final Context ctxt, @StringRes final int titleId,
        final TimePickerDialog.OnTimeSetListener listener) {
    final TimePickerDialog timePicker = new TimePickerDialog(ctxt, THEME_HOLO_DARK, listener, 12, 0, DateFormat.is24HourFormat(ctxt)); // todo: this isn't respecting the system setting (LineageOS, holo-dark style)
    // should there be an option for default time to be noon vs. the current time? noon seems much more reasonable in all cases tbh. infinitely more predictable—who the heck wants to set a timer for right now?!
    timePicker.setTitle(titleId);
    return timePicker;
}

public static AlertDialog.Builder withIntents(final AlertDialog.Builder builder,
        final AssistActivity act, final CharSequence[] labels, final Intent[] intents) {
    return builder.setItems(labels, (dialog, which) -> act.succeed(intents[which]));
}

public static AlertDialog.Builder withNullableIntents(final AlertDialog.Builder builder,
        final AssistActivity act, final CharSequence[] labels, final Intent[] intents,
        final CharSequence failMsg) {
    return builder.setItems(labels, (dialog, which) -> {
        final Intent in = intents[which];
        if (in == null) act.fail(failMsg);
        else act.succeed(in);
    });
}

private static AlertDialog.Builder withApps(final AlertDialog.Builder builder,
        final AssistActivity act, final PackageManager pm, final List<ResolveInfo> appList) {
    return withIntents(builder, act, Apps.labels(appList, pm), Apps.intents(appList));
}

public static AlertDialog.Builder appChooser(final AssistActivity act, final PackageManager pm,
        final List<ResolveInfo> appList) {
    // TODO: due to duplicate app labels, it's important to eventually include app icons in this dialog
    // TODO: allow for alpha sort. important for screen readers (it should be on by default in that case)
    //  only reason not to is i kind of like the random order. makes my phone feel less cramped.
    //  on the accessibility topic, being able to speak/type a (nato?) letter would be helpful for everyone.
    //  basically: very accessible search interface
    // a choice between list and grid layout would be cool
    final AlertDialog.Builder base = base(act, R.string.dialog_app);
    return withApps(base, act, pm, appList);
}

private static AlertDialog.Builder withUninstalls(final AlertDialog.Builder builder,
        final AssistActivity act, final PackageManager pm, final List<ResolveInfo> appList) {
    return withNullableIntents(builder, act, Apps.labels(appList, pm), Apps.uninstalls(appList, pm),
            "No settings app found for your device"); // Todo: instead handle at mapping somehow
}

public static AlertDialog.Builder appUninstaller(final AssistActivity act,
        final List<ResolveInfo> appList) {
    final AlertDialog.Builder base = base(act, R.string.dialog_app);
    return withUninstalls(base, act, act.getPackageManager(), appList);
}

private Dialogs() {}
}
