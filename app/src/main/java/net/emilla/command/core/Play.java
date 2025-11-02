package net.emilla.command.core;

import android.media.AudioManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.MediaControl;
import net.emilla.util.Services;

/*internal*/ final class Play extends CoreCommand {

    public static final String ENTRY = "play";

    public static boolean possible() {
        return true;
    }

    /*internal*/ Play(AssistActivity act) {
        super(act, CoreEntry.PLAY, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        AudioManager am = Services.audio(this.activity);
        MediaControl.sendPlayEvent(am);
        give(act -> {});
    }

    @Override
    protected void run(String media) {
        throw badCommand(R.string.error_unfinished_feature);
    }

}
