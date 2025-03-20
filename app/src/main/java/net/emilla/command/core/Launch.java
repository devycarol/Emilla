package net.emilla.command.core;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;

public final class Launch extends OpenCommand {

    public static final String ENTRY = "launch";
    @StringRes
    public static final int NAME = R.string.command_launch;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_launch;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Launch::new, ENTRY, NAME, ALIASES);
    }

    public Launch(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_app,
              R.drawable.ic_launch,
              R.string.summary_launch,
              R.string.manual_launch,
              EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        offerDialog(appChooser);
    }

    @Override
    protected Intent makeIntent(String pkg, String cls) {
        return Apps.launchIntent(pkg, cls);
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        return Dialogs.appLaunches(activity, pm(), appList);
    }
}
