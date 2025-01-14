package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Discord extends AppSend {

    public static final String PKG = "com.discord";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_discord;

    private static class DiscordParams extends AppSendParams {

        protected DiscordParams(Yielder info) {
            super(info,
                  R.string.instruction_message,
                  R.string.summary_messaging,
                  R.string.manual_app_send);
        }
    }

    public Discord(AssistActivity act, Yielder info) {
        super(act, new DiscordParams(info));
    }
}
