package net.emilla.command.app;

import android.view.inputmethod.EditorInfo;

import net.emilla.annotation.internal;
import net.emilla.wadget.ActionSurface;

final class VideoSearchBySend extends AppSend {
    @internal VideoSearchBySend(ActionSurface surface, AppEntry appEntry) {
        super(surface, appEntry, EditorInfo.IME_ACTION_SEARCH);
    }
}
