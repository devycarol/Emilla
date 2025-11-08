package net.emilla.command.core;

import android.content.Intent;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.app.AppEntry;
import net.emilla.struct.IndexedStruct;
import net.emilla.struct.sort.SearchResult;
import net.emilla.util.Dialogs;

/*internal*/ abstract class OpenCommand extends CoreCommand {

    protected OpenCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);
    }

    protected AlertDialog.Builder appChooser = null;

    @Override @CallSuper
    protected final void onInit() {
        super.onInit();
        this.appChooser = makeChooser();
    }

    @Override @CallSuper
    protected final void onClean() {
        super.onClean();
        this.appChooser = null;
    }

    protected abstract AlertDialog.Builder makeChooser();

    protected final void appSearchRun(String search, IntentMaker maker) {
        SearchResult<AppEntry> result = this.activity.appList().filter(search);
        int size = result.size();
        if (size == 0) {
            throw badCommand(R.string.error_apps_not_found);
            // Todo: offer to search app store
        }

        if (size == 1 || oneExactMatch(result, search)) {
            open(result, 0, maker);
        } else {
            offerDialog(Dialogs.list(this.activity, R.string.dialog_app, AppEntry.labels(result),
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
