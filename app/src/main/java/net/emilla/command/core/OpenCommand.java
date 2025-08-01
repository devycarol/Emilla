package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppEntry;
import net.emilla.struct.IndexedStruct;
import net.emilla.struct.sort.SearchResult;
import net.emilla.util.Dialogs;

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
        SearchResult<AppEntry> result = activity.appList().filter(search);
        int size = result.size();
        if (size == 0) {
            throw badCommand(R.string.error_apps_not_found);
            // Todo: offer to search app store
        }

        if (size == 1 || oneExactMatch(result, search)) {
            open(result, 0, maker);
        } else {
            offerDialog(Dialogs.list(activity, R.string.dialog_app, AppEntry.labels(result),
                                     (dlg, which) -> open(result, which, maker)));
        }
    }

    private static boolean oneExactMatch(SearchResult<AppEntry> result, String search) {
        return result.get(0).ordinalIs(search)
            && !result.get(1).ordinalIs(search);
    }

    private void open(IndexedStruct<AppEntry> filtered, int which, IntentMaker action) {
        AppEntry app = filtered.get(which);
        appSucceed(action.make(app));
    }

    @FunctionalInterface
    protected interface IntentMaker {
        Intent make(AppEntry app);
    }
}
