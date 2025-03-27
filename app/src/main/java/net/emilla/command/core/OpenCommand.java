package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppEntry;
import net.emilla.util.Dialogs;

import java.util.List;

public abstract class OpenCommand extends CoreCommand {

    protected OpenCommand(
        AssistActivity act,
        @StringRes int name,
        @StringRes int instruction,
        @DrawableRes int icon,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        super(act, name,
              instruction,
              icon,
              summary,
              manual,
              imeAction);
    }

    protected AlertDialog.Builder appChooser;

    @Override @CallSuper
    protected void onInit() {
        super.onInit();
        appChooser = makeChooser();
    }

    @Override @CallSuper
    protected void onClean() {
        super.onClean();
        appChooser = null;
    }

    protected abstract AlertDialog.Builder makeChooser();

    protected final void appSearchRun(String search, IntentMaker maker) {
        List<AppEntry> filtered = activity.appList().filter(search);
        switch (filtered.size()) {
        case 0 -> throw badCommand(R.string.error_apps_not_found);
        // Todo: offer to search app store
        case 1 -> open(filtered, 0, maker);
        default -> offerDialog(Dialogs.list(activity, R.string.dialog_app, AppEntry.labels(filtered),
                                            (dlg, which) -> open(filtered, which, maker)));
        }
    }

    private void open(List<AppEntry> filtered, int which, IntentMaker action) {
        AppEntry app = filtered.get(which);
        appSucceed(action.make(app));
    }

    @FunctionalInterface
    protected interface IntentMaker {
        Intent make(AppEntry app);
    }
}
