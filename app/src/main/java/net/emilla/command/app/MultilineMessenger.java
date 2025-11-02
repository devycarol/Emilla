package net.emilla.command.app;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

/*internal*/ final class MultilineMessenger extends AppSendData {

    /*internal*/ MultilineMessenger(AssistActivity act, AppEntry appEntry) {
        super(act, appEntry, R.string.data_hint_message_cont);
    }

}
