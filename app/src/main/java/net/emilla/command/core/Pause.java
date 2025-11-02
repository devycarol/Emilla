package net.emilla.command.core;

import android.media.AudioManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.MediaControl;
import net.emilla.util.Services;

/*internal*/ final class Pause extends CoreCommand {

    public static final String ENTRY = "pause";

    public static boolean possible() {
        return true;
    }

    /*internal*/ Pause(AssistActivity act) {
        super(act, CoreEntry.PAUSE, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        AudioManager am = Services.audio(this.activity);
        MediaControl.sendPauseEvent(am);
        give(act -> {});
    }

    @Override
    protected void run(String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }

}
