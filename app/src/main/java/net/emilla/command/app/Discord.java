package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Discord extends AppSend {

    public static final String PKG = "com.discord";

    private static class DiscordParams extends AppSendParams {

        protected DiscordParams(AppInfo info) {
            super(info,
                  R.string.instruction_message,
                  R.string.summary_messaging,
                  R.string.manual_app_send);
        }
    }

    public Discord(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new DiscordParams(info));
    }
}
