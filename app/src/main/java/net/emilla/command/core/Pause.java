package net.emilla.command.core;

import android.media.AudioManager;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.media.MediaControl;
import net.emilla.util.Services;
import net.emilla.wadget.ActionSurface;

final class Pause extends EmillaCommand {
    @internal Pause(ActionSurface surface) {
        super(surface, CoreEntry.PAUSE, ImeAction.DO);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        AudioManager audio = Services.audio(surface.getContext());
        MediaControl.pause(audio);
        return Feedback.give();
    }

    @Override
    protected Feedback run(ActionSurface surface, String ignored) {
        return run(surface); // Todo: remove this from the interface for non-instructables.
    }
}
