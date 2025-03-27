package net.emilla.action;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.exception.EmillaException;
import net.emilla.run.MessageFailure;
import net.emilla.util.TorchManager;

public final class Flashlight implements LabeledQuickAction {

    @StringRes
    private static final int NAME = R.string.action_flashlight;

    private final AssistActivity mActivity;

    public Flashlight(AssistActivity act) {
        mActivity = act;
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
        return NAME;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_flashlight;
    }

    @Override
    public void perform() { try {
        TorchManager.toggle(mActivity, NAME);
    } catch (EmillaException e) {
        mActivity.fail(new MessageFailure(mActivity, e));
    }}
}
