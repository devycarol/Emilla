package net.emilla.action;

import static net.emilla.chime.Chimer.ACT;

import android.content.Intent;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.config.ConfigActivity;
import net.emilla.utils.Apps;

public class AssistantSettings implements QuickAction {

    private final AssistActivity mActivity;

    public AssistantSettings(AssistActivity act) {
        mActivity = act;
    }

    @Override
    public int icon() {
        return R.drawable.ic_assistant;
    }

    @Override
    public void perform() {
        Intent config = Apps.meTask(mActivity, ConfigActivity.class);
        if (mActivity.shouldCancel()) mActivity.succeed(config);
        else {
            mActivity.suppressPendingChime();
            mActivity.startActivity(config);
            mActivity.chime(ACT);
        }
    }
}
