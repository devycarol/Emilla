package net.emilla.command.app;

import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

@open class AppSend extends AppCommand {
    @internal AppSend(ActionSurface surface, AppEntry appEntry) {
        this(surface, appEntry, EditorInfo.IME_ACTION_SEND);
    }

    @internal AppSend(ActionSurface surface, AppEntry appEntry, int imeAction) {
        super(surface, appEntry, imeAction);
    }

    @Override
    protected final Feedback run(ActionSurface surface, String message) {
        Intent intent = Intents.sendToApp(this.appEntry.pkg)
            .putExtra(EXTRA_TEXT, message)
        ;
        return Feedback.succeed(intent);
    }
}
