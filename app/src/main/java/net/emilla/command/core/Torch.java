package net.emilla.command.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Features;
import net.emilla.util.TorchManager;

/*internal*/ final class Torch extends CoreCommand {

    public static boolean possible(PackageManager pm) {
        return Features.torch(pm);
    }

    /*internal*/ Torch(Context ctx) {
        super(ctx, CoreEntry.TORCH, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        TorchManager.toggle(act, CoreEntry.TORCH.name);
    }

    @Override
    protected void run(AssistActivity act, String ignored) {
        run(act); // Todo: remove this from the interface for non-instructables.
    }

}
