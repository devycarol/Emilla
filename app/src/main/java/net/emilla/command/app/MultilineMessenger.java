package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

abstract class MultilineMessenger extends AppSendData {

    MultilineMessenger(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_message,
              R.string.summary_messaging,
              R.string.data_hint_message_cont);
    }
}
