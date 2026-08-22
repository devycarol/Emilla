package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.action.box.AppsFragment;
import net.emilla.command.EmillaCommand;
import net.emilla.command.app.AppEntry;
import net.emilla.wadget.ActionSurface;

public abstract class OpenCommand extends EmillaCommand {
    private final AppsFragment mAppsFragment = AppsFragment.newInstance();

    protected OpenCommand(
        ActionSurface surface,
        CoreEntry coreEntry,
        int imeAction
    ) {
        super(surface, coreEntry, imeAction);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mAppsFragment,
        };
    }

    @Override
    protected Gadget[] gadgets() {
        return new Gadget[] {
            mAppsFragment,
        };
    }

    @Nullable
    protected abstract Intent defaultIntent();
    protected abstract Intent makeIntent(AppEntry app, PackageManager pm);

    public final Feedback use(ActionSurface surface, AppEntry app) {
        var pm = surface.getPackageManager();
        return Feedback.succeed(makeIntent(app, pm));
    }

    @Override
    protected final Feedback run(ActionSurface surface) {
        var intent = defaultIntent();
        return intent != null
            ? Feedback.succeed(intent)
            : Feedback.pend()
        ;
    }

    @Override
    protected final Feedback run(ActionSurface surface, String instruction) {
        AppEntry app = mAppsFragment.selectedApp(instruction);
        return app != null
            ? use(surface, app)
            : Feedback.pend()
        ;
    }
}
