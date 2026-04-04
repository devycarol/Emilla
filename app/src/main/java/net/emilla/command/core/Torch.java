package net.emilla.command.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.util.Features;
import net.emilla.util.TorchManager;

final class Torch extends CoreCommand {
    public static boolean possible(PackageManager pm) {
        return Features.torch(pm);
    }

    @internal Torch(Context ctx) {
        super(ctx, CoreEntry.TORCH, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        if (!TorchManager.toggle(act)) {
            fail(act, R.string.error_torch_failed);
        }
    }

    @Override
    protected void run(AssistActivity act, String ignored) {
        run(act); // Todo: remove this from the interface for non-instructables.
    }
}
