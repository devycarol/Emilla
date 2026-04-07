package net.emilla.action;

import android.media.AudioManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.media.MediaControl;
import net.emilla.util.Services;

public final class PlayPause implements LabeledQuickAction {
    private final AssistActivity mActivity;

    public PlayPause(AssistActivity act) {
        mActivity = act;
    }

    @Override @IdRes
    public int id() {
        return R.id.action_flashlight;
    }

    @Override @DrawableRes
    public int symbol() {
        // TODO: update all feedbacks when media starts and stops playing, either from our actions
        //  or outside.
        return R.drawable.ic_play;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_play_pause;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_play_pause;
    }

    @Override
    public void perform() {
        AudioManager audio = Services.audio(mActivity);
        MediaControl.playPause(audio);
    }
}
