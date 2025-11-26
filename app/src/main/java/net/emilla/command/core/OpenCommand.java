package net.emilla.command.core;

import static net.emilla.chime.Chime.PEND;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import net.emilla.action.box.AppsFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.command.app.AppEntry;

public abstract class OpenCommand extends CoreCommand {

    private final AppsFragment mAppsFragment;

    protected OpenCommand(Context ctx, CoreEntry coreEntry, int imeAction) {
        super(ctx, coreEntry, imeAction);

        mAppsFragment = AppsFragment.newInstance();
        giveGadgets(mAppsFragment);
    }

    @Nullable
    protected abstract Intent defaultIntent();
    protected abstract Intent makeIntent(AppEntry app, PackageManager pm);

    public final void use(AssistActivity act, AppEntry app) {
        var pm = act.getPackageManager();
        appSucceed(act, makeIntent(app, pm));
    }

    @Override
    protected final void run(AssistActivity act) {
        var intent = defaultIntent();
        if (intent != null) {
            appSucceed(act, intent);
        } else {
            act.chime(PEND);
        }
    }

    @Override
    protected final void run(AssistActivity act, String instruction) {
        AppEntry app = mAppsFragment.selectedApp(instruction);
        if (app != null) {
            use(act, app);
        } else {
            act.chime(PEND);
        }
    }

}
