package net.emilla.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.run.AppSuccess;
import net.emilla.run.MessageFailure;
import net.emilla.app.Apps;

import java.util.List;

public final class Dialogs {

    private static AlertDialog.Builder base(Context ctx, CharSequence title, @StringRes int noLabel) {
        return new AlertDialog.Builder(ctx).setTitle(title)
                .setNegativeButton(noLabel, (dlg, which) -> dlg.cancel());
        // Todo: don't require to call the cancel listener.
    }

    private static AlertDialog.Builder base(
        Context ctx,
        @StringRes int title,
        @StringRes int noLabel
    ) {
        return new AlertDialog.Builder(ctx).setTitle(title)
                .setNegativeButton(noLabel, (dlg, which) -> dlg.cancel());
        // Todo: don't require to call the cancel listener.
    }

    public static AlertDialog.Builder base(
        Context ctx,
        @StringRes int title,
        @StringRes int msg,
        @StringRes int noLabel
    ) {
        return base(ctx, title, noLabel).setMessage(msg);
    }

    public static AlertDialog.Builder base(
        Context ctx,
        @StringRes int title,
        CharSequence msg,
        @StringRes int noLabel
    ) {
        return base(ctx, title, noLabel).setMessage(msg);
    }

    public static AlertDialog.Builder base(
        Context ctx,
        CharSequence title,
        @StringRes int msg,
        @StringRes int noLabel
    ) {
        return base(ctx, title, noLabel).setMessage(msg);
    }

    public static AlertDialog.Builder base(
        Context ctx,
        CharSequence title,
        CharSequence msg,
        @StringRes int noLabel
    ) {
        return base(ctx, title, noLabel).setMessage(msg);
    }

    public static AlertDialog.Builder message(Context ctx, @StringRes int title, @StringRes int msg) {
        return base(ctx, title, android.R.string.ok).setMessage(msg);
    }

    public static AlertDialog.Builder message(Context ctx, @StringRes int title, CharSequence msg) {
        return base(ctx, title, android.R.string.ok).setMessage(msg);
    }

    public static AlertDialog.Builder message(Context ctx, CharSequence title, @StringRes int msg) {
        return base(ctx, title, android.R.string.ok).setMessage(msg);
    }

    public static AlertDialog.Builder message(Context ctx, CharSequence title, CharSequence msg) {
        return base(ctx, title, android.R.string.ok).setMessage(msg);
    }

    public static AlertDialog.Builder list(
        AssistActivity act,
        @StringRes int title,
        CharSequence[] labels,
        DialogInterface.OnClickListener onChoose
    ) {
        return base(act, title, android.R.string.cancel).setItems(labels, (dlg, which) -> {
            onChoose.onClick(dlg, which);
            act.onCloseDialog(); // Todo: don't require this.
        });
        // TODO ACC: the cancel button is destroyed when the list is bigger than the screen for some
        //  reason
    }

    public static AlertDialog.Builder dual(
        AssistActivity act,
        @StringRes int title,
        @StringRes int msg,
        @StringRes int yesLabel,
        DialogInterface.OnClickListener yesClick
    ) {
        return base(act, title, msg, android.R.string.cancel)
                .setPositiveButton(yesLabel, (dlg, which) -> {
            yesClick.onClick(dlg, which);
            act.onCloseDialog(); // Todo: don't require this.
        });
    }

    public static AlertDialog.Builder dual(
        AssistActivity act,
        @StringRes int title,
        CharSequence msg,
        @StringRes int yesLabel,
        DialogInterface.OnClickListener yesClick
    ) {
        return base(act, title, msg, android.R.string.cancel)
                .setPositiveButton(yesLabel, (dlg, which) -> {
            yesClick.onClick(dlg, which);
            act.onCloseDialog(); // Todo: don't require this.
        });
    }

    public static AlertDialog.Builder dual(
        AssistActivity act,
        CharSequence title,
        @StringRes int msg,
        @StringRes int yesLabel,
        DialogInterface.OnClickListener yesClick
    ) {
        return base(act, title, msg, android.R.string.cancel)
                .setPositiveButton(yesLabel, (dlg, which) -> {
            yesClick.onClick(dlg, which);
            act.onCloseDialog(); // Todo: don't require this.
        });
    }

    public static AlertDialog.Builder appLaunches(AssistActivity act, PackageManager pm) {
        return appLaunches(act, pm, act.appList());
    }

    public static AlertDialog.Builder appLaunches(
        AssistActivity act,
        PackageManager pm,
        List<ResolveInfo> appList
    ) {
        // TODO: due to duplicate app labels, it's important to eventually include app icons in this
        //  dialog
        // TODO: allow for alpha sort. important for screen readers (it should be on by default in
        //  that case) only reason not to is i kind of like the random order. makes my phone feel
        //  less cramped. on the accessibility topic, being able to speak/type a (nato?) letter
        //  would be helpful for everyone. basically: very accessible search interface
        // a choice between list and grid layout would be cool
        Intent[] intents = Apps.launches(appList);
        return list(act, R.string.dialog_app, Apps.labels(appList, pm),
                (dlg, which) -> act.succeed(new AppSuccess(act, intents[which])));
    }

    public static AlertDialog.Builder appUninstalls(AssistActivity act) {
        List<ResolveInfo> appList = act.appList();
        var pm = act.getPackageManager();
        Intent[] intents = Apps.uninstalls(appList, pm);
        return list(act, R.string.dialog_app, Apps.labels(appList, pm), (dlg, which) -> {
            if (intents[which] == null) {
                act.fail(new MessageFailure(act, R.string.command_uninstall,
                                            R.string.error_cant_uninstall));
                // Todo: instead handle at mapping somehow
            } else {
                act.succeed(new AppSuccess(act, intents[which]));
            }
        });
    }

    private Dialogs() {}
}
