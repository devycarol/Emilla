package net.emilla.command.app;

import static android.content.Intent.EXTRA_TEXT;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Intents;

/*internal open*/ class AppSend extends AppCommand {

    /*internal*/ AppSend(Context ctx, AppEntry appEntry) {
        this(ctx, appEntry, EditorInfo.IME_ACTION_SEND);
    }

    /*internal*/ AppSend(Context ctx, AppEntry appEntry, int imeAction) {
        super(ctx, appEntry, imeAction);
    }

    @Override
    protected final void run(AssistActivity act, String message) {
        appSucceed(act, Intents.sendToApp(this.appEntry.pkg).putExtra(EXTRA_TEXT, message));
    }

}
