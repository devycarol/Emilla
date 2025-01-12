package net.emilla.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.run.AppSuccess;
import net.emilla.run.MessageFailure;

import java.util.List;

public final class Dialogs {

    private static AlertDialog.Builder base(Context ctx, CharSequence title, @StringRes int negLabel) {
        return new AlertDialog.Builder(ctx).setTitle(title)
                .setNegativeButton(negLabel, (dlg, which) -> dlg.cancel());
        // Todo: don't require to call the cancel listener
    }

    private static AlertDialog.Builder base(Context ctx, @StringRes int title,
            @StringRes int negLabel) {
        return new AlertDialog.Builder(ctx).setTitle(title)
                .setNegativeButton(negLabel, (dlg, which) -> dlg.cancel());
        // Todo: don't require to call the cancel listener
    }

    public static AlertDialog.Builder base(Context ctx, @StringRes int title, @StringRes int msg,
            @StringRes int negLabel) {
        return base(ctx, title, negLabel).setMessage(msg);
    }

    public static AlertDialog.Builder base(Context ctx, CharSequence title, @StringRes int msg,
            @StringRes int negLabel) {
        return base(ctx, title, negLabel).setMessage(msg);
    }

    public static AlertDialog.Builder base(Context ctx, CharSequence title, CharSequence msg,
            @StringRes int negLabel) {
        return base(ctx, title, negLabel).setMessage(msg);
    }

    public static AlertDialog.Builder listBase(Context ctx, @StringRes int title) {
        return base(ctx, title, android.R.string.cancel);
        // TODO ACC: the cancel button is destroyed when the list is bigger than the screen for some
        //  reason
    }

    public static AlertDialog.Builder dual(Context ctx, @StringRes int title, @StringRes int msg,
            @StringRes int posLabel, DialogInterface.OnClickListener yesClick) {
        return dual(ctx, title, msg, posLabel, android.R.string.cancel, yesClick);
    }

    public static AlertDialog.Builder dual(Context ctx, @StringRes int title, @StringRes int msg,
            @StringRes int posLabel, @StringRes int negLabel,
            DialogInterface.OnClickListener yesClick) {
        return base(ctx, title, msg, negLabel).setPositiveButton(posLabel, yesClick);
    }

    public static AlertDialog.Builder yesNo(Context ctx, @StringRes int title, @StringRes int msg,
            DialogInterface.OnClickListener yesClick) {
        return dual(ctx, title, msg, R.string.yes, R.string.no, yesClick);
    }

    public static AlertDialog.Builder withIntents(AlertDialog.Builder builder, AssistActivity act,
            CharSequence[] labels, Intent[] intents) {
        return builder.setItems(labels, (dlg, which) -> act.succeed(new AppSuccess(act, intents[which])));
    }

    private static AlertDialog.Builder withApps(AlertDialog.Builder builder, AssistActivity act,
            PackageManager pm, List<ResolveInfo> appList) {
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
        AlertDialog.Builder base = listBase(act, R.string.dialog_app);
        return withApps(base, act, pm, appList);
    }

    private static AlertDialog.Builder withUninstalls(AlertDialog.Builder builder,
            AssistActivity act, PackageManager pm, List<ResolveInfo> appList) {
        CharSequence[] labels = Apps.labels(appList, pm);
        Intent[] intents = Apps.uninstalls(appList, pm);
        return builder.setItems(labels, (dlg, which) -> {
            if (intents[which] == null) act.fail(new MessageFailure(act, R.string.command_uninstall,
                    R.string.error_cant_uninstall));
            // Todo: instead handle at mapping somehow
            else act.succeed(new AppSuccess(act, intents[which]));
        });
    }

    public static AlertDialog.Builder appUninstaller(AssistActivity act, List<ResolveInfo> appList) {
        AlertDialog.Builder base = listBase(act, R.string.dialog_app);
        return withUninstalls(base, act, act.getPackageManager(), appList);
    }

    private Dialogs() {}
}
