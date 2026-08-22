package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.action.box.AppsFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.command.EmillaCommand;
import net.emilla.command.app.AppEntry;
import net.emilla.wadget.ActionSurface;

public abstract class OpenCommand extends EmillaCommand {
    private final AppsFragment mAppsFragment;

    protected OpenCommand(Context ctx, CoreEntry coreEntry, int imeAction) {
        super(ctx, coreEntry, imeAction);

        mAppsFragment = AppsFragment.newInstance();
        giveGadgets(mAppsFragment);
    }

    @Nullable
    protected abstract Intent defaultIntent();
    protected abstract Intent makeIntent(AppEntry app, PackageManager pm);

    public final Feedback use(AssistActivity act, AppEntry app) {
        var pm = act.getPackageManager();
        return Feedback.succeed(makeIntent(app, pm));
    }

    @Override
    protected final Feedback run(ActionSurface surface, AssistActivity act) {
        var intent = defaultIntent();
        return intent != null
            ? Feedback.succeed(intent)
            : Feedback.pend()
        ;
    }

    @Override
    protected final Feedback run(ActionSurface surface, AssistActivity act, String instruction) {
        AppEntry app = mAppsFragment.selectedApp(instruction);
        return app != null
            ? use(act, app)
            : Feedback.pend()
        ;
    }
}
