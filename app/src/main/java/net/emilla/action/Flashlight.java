package net.emilla.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.TorchManager;

public final class Flashlight implements LabeledQuickAction {
    private final AssistActivity mActivity;

    public Flashlight(AssistActivity act) {
        mActivity = act;
    }

    @Override @IdRes
    public int id() {
        return R.id.action_flashlight;
    }

    @Override @DrawableRes
    public int symbol() {
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
        if (!TorchManager.toggle(mActivity)) {
            mActivity.fail(R.string.action_flashlight, R.string.error_torch_failed);
        }
    }
}
