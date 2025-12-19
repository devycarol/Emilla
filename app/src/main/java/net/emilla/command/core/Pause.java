package net.emilla.command.core;

import android.content.Context;
import android.media.AudioManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.util.MediaControl;
import net.emilla.util.Services;

final class Pause extends CoreCommand {

    @internal Pause(Context ctx) {
        super(ctx, CoreEntry.PAUSE, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        AudioManager am = Services.audio(act);
        MediaControl.sendPauseEvent(am);
        act.give(a -> {});
    }

    @Override
    protected void run(AssistActivity act, String ignored) {
        run(act); // Todo: remove this from the interface for non-instructables.
    }

}
