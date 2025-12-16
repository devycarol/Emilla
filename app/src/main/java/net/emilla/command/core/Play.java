package net.emilla.command.core;

import android.content.Context;
import android.media.AudioManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.MediaControl;
import net.emilla.util.Services;

final class Play extends CoreCommand {

    /*internal*/ Play(Context ctx) {
        super(ctx, CoreEntry.PLAY, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run(AssistActivity act) {
        AudioManager am = Services.audio(act);
        MediaControl.sendPlayEvent(am);
        act.give(a -> {});
    }

    @Override
    protected void run(AssistActivity act, String media) {
        throw badCommand(R.string.error_unfinished_feature);
    }

}
