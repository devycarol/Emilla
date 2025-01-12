package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

abstract class MultilineMessenger extends AppSendData {

    private static class MultilineMessengerParams extends AppSendDataParams {

        private MultilineMessengerParams(AppInfo info) {
            super(info,
                  R.string.instruction_message,
                  R.string.summary_messaging,
                  R.string.data_hint_message_cont);
        }
    }

    MultilineMessenger(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new MultilineMessengerParams(info));
    }
}
