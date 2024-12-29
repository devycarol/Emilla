package net.emilla.action;

import static net.emilla.chime.Chimer.PEND;

import android.content.res.Resources;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class NoAction implements QuickAction {

    private final AssistActivity mActivity;
    private final Resources mRes;

    public NoAction(AssistActivity act) {
        mActivity = act;
        mRes = act.getResources();
    }

    @Override @IdRes
    public int id() {
        return R.id.action_none;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_assistant;
    }

    @Override
    public String label() {
        return mRes.getString(R.string.action_none);
    }

    @Override
    public String description() {
        return mRes.getString(R.string.action_desc_none);
    }

    @Override
    public void perform() {
        mActivity.chime(PEND);
    }
}
