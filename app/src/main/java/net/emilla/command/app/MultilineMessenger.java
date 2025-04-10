package net.emilla.command.app;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

/*internal*/ abstract class MultilineMessenger extends AppSendData {

    public MultilineMessenger(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_message,
              R.string.summary_messaging,
              R.string.data_hint_message_cont);
    }
}
