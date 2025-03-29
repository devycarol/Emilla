package net.emilla.command.core;

import static android.content.Intent.ACTION_UNINSTALL_PACKAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;
import net.emilla.util.Dialogs;

public final class Uninstall extends OpenCommand {

    public static final String ENTRY = "uninstall";
    @StringRes
    public static final int NAME = R.string.command_uninstall;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_uninstall;

    public static Yielder yielder() {
        return new Yielder(true, Uninstall::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible(PackageManager pm) {
        return canDo(pm, new Intent(ACTION_UNINSTALL_PACKAGE, Apps.pkgUri("")))
            // todo: ACTION_UNINSTALL_PACKAGE is deprecated?
            || canDo(pm, Apps.infoTask(""))
            || canDo(pm, new Intent(Settings.ACTION_SETTINGS));
    }

    private Uninstall(AssistActivity act) {
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
        appSearchRun(app, (appEntry) -> Apps.uninstallIntent(appEntry.pkg, pm()));
    }

    @Override
    protected AlertDialog.Builder makeChooser() {
        return Dialogs.appUninstalls(activity);
    }
}
