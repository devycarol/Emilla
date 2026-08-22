package net.emilla.command.app;

import static android.content.Intent.EXTRA_TEXT;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

@open class AppSend extends AppCommand {
    @internal AppSend(Context ctx, AppEntry appEntry) {
        this(ctx, appEntry, EditorInfo.IME_ACTION_SEND);
    }

    @internal AppSend(Context ctx, AppEntry appEntry, int imeAction) {
        super(ctx, appEntry, imeAction);
    }

    @Override
    protected final Feedback run(ActionSurface surface, AssistActivity act, String message) {
        Intent intent = Intents.sendToApp(this.appEntry.pkg)
            .putExtra(EXTRA_TEXT, message)
        ;
        return Feedback.succeed(intent);
    }
}
