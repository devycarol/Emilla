package net.emilla.command.core;

import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Features;
import net.emilla.util.TorchManager;

/*internal*/ final class Torch extends CoreCommand {

    public static final String ENTRY = "torch";

    public static boolean possible(PackageManager pm) {
        return Features.torch(pm);
    }

    /*internal*/ Torch(AssistActivity act) {
        super(act, CoreEntry.TORCH, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        TorchManager.toggle(this.activity, CoreEntry.TORCH.name);
    }

    @Override
    protected void run(String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }

}
