package net.emilla.command.core;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.app.AppEntry;
import net.emilla.struct.IndexedStruct;
import net.emilla.util.Dialogs;

/*internal*/ abstract class OpenCommand extends CoreCommand {

    protected final AlertDialog.Builder appChooser;

    protected OpenCommand(AssistActivity act, CoreEntry coreEntry, int imeAction) {
        super(act, coreEntry, imeAction);

        this.appChooser = makeChooser(act);
    }

    protected abstract AlertDialog.Builder makeChooser(AssistActivity act);

    protected final void appSearchRun(AssistActivity act, String search, IntentMaker maker) {
        IndexedStruct<AppEntry> result = act.appList().filter(search);
        int size = result.size();
        if (size == 0) {
            throw badCommand(R.string.error_apps_not_found);
            // Todo: offer to search app store
        }

        if (size == 1 || oneExactMatch(result, search)) {
            open(act, result, 0, maker);
        } else {
            offerDialog(
                act,
                Dialogs.list(
                    act, R.string.dialog_app,

                    AppEntry.labels(result),
                    (dlg, which) -> open(act, result, which, maker)
                )
            );
        }
    }

    private static boolean oneExactMatch(IndexedStruct<AppEntry> result, String search) {
        return result.get(0).displayName.equalsIgnoreCase(search)
            && !result.get(1).displayName.equalsIgnoreCase(search);
    }

    private static void open(
        AssistActivity act,
        IndexedStruct<AppEntry> filtered,
        int which,
        IntentMaker action
    ) {
        AppEntry app = filtered.get(which);
        appSucceed(act, action.make(app));
    }

    @FunctionalInterface
    protected interface IntentMaker {
        Intent make(AppEntry app);
    }

}
