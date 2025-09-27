package net.emilla.command.core;

import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Features;
import net.emilla.util.TorchManager;

public final class Torch extends CoreCommand {

    public static final String ENTRY = "torch";
    @StringRes
    public static final int NAME = R.string.command_torch;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_torch;

    public static Yielder yielder() {
        return new Yielder(false, Torch::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible(PackageManager pm) {
        return Features.torch(pm);
    }

    private Torch(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_torch,
              R.drawable.ic_torch,
              R.string.summary_torch,
              R.string.manual_torch,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        TorchManager.toggle(pActivity, NAME);
    }

    @Override
    protected void run(String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }
}
