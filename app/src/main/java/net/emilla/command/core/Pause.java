package net.emilla.command.core;

import android.content.Context;
import android.media.AudioManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.media.MediaControl;
import net.emilla.util.Services;
import net.emilla.wadget.ActionSurface;

final class Pause extends EmillaCommand {
    @internal Pause(Context ctx) {
        super(ctx, CoreEntry.PAUSE, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        AudioManager audio = Services.audio(act);
        MediaControl.pause(audio);
        return Feedback.give();
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String ignored) {
        return run(surface, act); // Todo: remove this from the interface for non-instructables.
    }
}
