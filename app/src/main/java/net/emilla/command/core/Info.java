package net.emilla.command.core;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;

public class Info extends OpenCommand {

    public static final String ENTRY = "info";
    @StringRes
    public static final int NAME = R.string.command_info;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_info;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Info::new, ENTRY, NAME, ALIASES);
    }

    private static class InfoParams extends CoreParams {

        private InfoParams() {
            super(NAME,
                  R.string.instruction_app,
                  R.drawable.ic_info,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_info,
                  R.string.manual_info);
        }
    }

    public Info(AssistActivity act) {
        super(act, new InfoParams());
    }

    @Override
    protected void run() {
        // Todo: it may be useful to include listings beyond those in the launcher icons, or be able to
        //  search by package name.
        appSucceed(Apps.infoTask());
    }

    @Override
    protected Intent makeIntent(String pkg, String cls) {
        return Apps.infoTask(pkg);
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        return Dialogs.appLaunches(activity, pm(), appList);
    }
}
