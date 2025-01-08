package net.emilla.action;

import static net.emilla.chime.Chimer.PEND;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class NoAction implements LabeledQuickAction {

    private final AssistActivity mActivity;

    public NoAction(AssistActivity act) {
        mActivity = act;
    }

    @Override @IdRes
    public int id() {
        return R.id.action_none;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_assistant;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_none;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_none;
    }

    @Override
    public void perform() {
        mActivity.chime(PEND);
    }
}
