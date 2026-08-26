package net.emilla.command.core;

import android.content.pm.PackageManager;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.util.Features;
import net.emilla.util.TorchManager;
import net.emilla.wadget.ActionSurface;

final class Torch extends EmillaCommand {
    public static boolean possible(PackageManager pm) {
        return Features.torch(pm);
    }

    @internal Torch(ActionSurface surface) {
        super(surface, CoreEntry.TORCH);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return TorchManager.toggle(surface.getAssistActivity())
            ? Feedback.silence()
            : Feedback.fail(R.string.error_torch_failed)
        ;
    }

    @Override
    protected Feedback run(ActionSurface surface, String ignored) {
        return run(surface); // Todo: remove this from the interface for non-instructables.
    }
}
