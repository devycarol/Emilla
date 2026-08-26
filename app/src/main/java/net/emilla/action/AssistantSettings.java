package net.emilla.action;

import android.content.Intent;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.chime.Chime;
import net.emilla.config.SettingsActivity;
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
    public int symbol() {
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
        Intent assistantSettings = Intents.me(mActivity, SettingsActivity.class);
        if (mActivity.shouldCancel()) {
            mActivity.take(Feedback.succeed(assistantSettings));
        } else {
            mActivity.suppressChime(Chime.PEND);
            mActivity.startActivity(assistantSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            mActivity.chime(Chime.ACT);
        }
    }
}
