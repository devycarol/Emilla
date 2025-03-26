package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;

public final class Uninstall extends OpenCommand {

    public static final String ENTRY = "uninstall";
    @StringRes
    public static final int NAME = R.string.command_uninstall;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_uninstall;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Uninstall::new, ENTRY, NAME, ALIASES);
    }

    public Uninstall(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_app,
              R.drawable.ic_uninstall,
              R.string.summary_uninstall,
              R.string.manual_uninstall,
              EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        offerDialog(appChooser);
    }

    @Override
    protected void run(String app) {
        appSearchRun(app, (pkg, cls) -> Apps.uninstallIntent(pkg, pm()));
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        return Dialogs.appUninstalls(activity);
    }
}
