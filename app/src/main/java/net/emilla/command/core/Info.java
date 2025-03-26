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

public final class Info extends OpenCommand {

    public static final String ENTRY = "info";
    @StringRes
    public static final int NAME = R.string.command_info;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_info;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Info::new, ENTRY, NAME, ALIASES);
    }

    public Info(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_app,
              R.drawable.ic_info,
              R.string.summary_info,
              R.string.manual_info,
              EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        // Todo: it may be useful to include listings beyond those in the launcher icons, or be able to
        //  search by package name.
        appSucceed(Apps.infoTask());
    }

    @Override
    protected void run(String app) {
        appSearchRun(app, (pkg, cls) -> Apps.infoTask(pkg));
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        return Dialogs.appLaunches(activity, pm());
    }
}
