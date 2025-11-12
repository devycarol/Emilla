package net.emilla.action;

import static net.emilla.chime.Chime.ACT;
import static net.emilla.chime.Chime.PEND;

import android.content.Intent;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.config.ConfigActivity;
import net.emilla.run.AppSuccess;
import net.emilla.util.Intents;

public final class AssistantSettings implements LabeledQuickAction {

    private final AssistActivity mActivity;

    public AssistantSettings(AssistActivity act) {
        mActivity = act;
    }

    @Override @IdRes
    public int id() {
        return R.id.action_assistant_settings;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_assistant;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_assistant_settings;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_assistant_settings;
    }

    @Override
    public void perform() {
        Intent config = Intents.me(mActivity, ConfigActivity.class);
        if (mActivity.shouldCancel()) {
            mActivity.succeed(new AppSuccess(config));
        } else {
            mActivity.suppressChime(PEND);
            mActivity.startActivity(config.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            mActivity.chime(ACT);
        }
    }
}
