package net.emilla.command.app;

import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.ImeAction;
import net.emilla.wadget.ActionSurface;

final class MultilineMessenger extends AppSendData {
    @internal MultilineMessenger(ActionSurface surface, AppEntry appEntry) {
        super(
            surface,
            appEntry,
            ImeAction.SEND,
            R.string.data_hint_message_cont
        );
    }
}
