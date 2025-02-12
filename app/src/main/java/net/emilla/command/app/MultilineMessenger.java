package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

abstract class MultilineMessenger extends AppSendData {

    private static final class MultilineMessengerParams extends AppSendDataParams {

        private MultilineMessengerParams(Yielder info) {
            super(info,
                  R.string.instruction_message,
                  R.string.summary_messaging,
                  R.string.data_hint_message_cont);
        }
    }

    MultilineMessenger(AssistActivity act, Yielder info) {
        super(act, new MultilineMessengerParams(info));
    }
}
