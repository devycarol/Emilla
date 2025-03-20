package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Discord extends AppSend {

    public static final String PKG = "com.discord";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_discord;

    public Discord(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_message,
              R.string.summary_messaging);
    }
}
