package net.emilla.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.core.Torch;

public class Flashlight implements LabeledQuickAction {

    private final Torch mTorch;

    public Flashlight(AssistActivity act) {
        mTorch = new Torch(act);
    }

    @Override @IdRes
    public int id() {
        return R.id.action_flashlight;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_torch;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_flashlight;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_flashlight;
    }

    @Override
    public void perform() {
        mTorch.execute();
    }
}
