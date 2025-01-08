package net.emilla.action;

import static net.emilla.chime.Chimer.ACT;

import android.content.Intent;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.config.ConfigActivity;
import net.emilla.run.AppSuccess;
import net.emilla.utils.Apps;

public class AssistantSettings implements LabeledQuickAction {

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
        Intent config = Apps.meTask(mActivity, ConfigActivity.class);
        if (mActivity.shouldCancel()) mActivity.succeed(new AppSuccess(mActivity, config));
        else {
            mActivity.suppressPendingChime();
            mActivity.startActivity(config.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            mActivity.chime(ACT);
        }
    }
}
