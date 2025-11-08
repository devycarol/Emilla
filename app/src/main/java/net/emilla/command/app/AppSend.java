package net.emilla.command.app;

import static android.content.Intent.EXTRA_TEXT;

import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Intents;

/*internal open*/ class AppSend extends AppCommand {

    /*internal*/ AppSend(AssistActivity act, AppEntry appEntry) {
        this(act, appEntry, EditorInfo.IME_ACTION_SEND);
    }

    /*internal*/ AppSend(AssistActivity act, AppEntry appEntry, int imeAction) {
        super(act, appEntry, imeAction);
    }

    @Override
    protected final void run(String message) {
        appSucceed(Intents.sendToApp(this.appEntry.pkg).putExtra(EXTRA_TEXT, message));
    }

}
