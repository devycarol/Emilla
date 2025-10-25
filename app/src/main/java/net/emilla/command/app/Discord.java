package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppProperties;

public final class Discord extends AppSend {

    public static final String PKG = "com.discord";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_discord;
    @StringRes
    private static final int SUMMARY = R.string.summary_messaging;

    public static AppProperties meta() {
        return AppProperties.ordinary(ALIASES, SUMMARY);
    }

    public Discord(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_message,
              R.string.summary_messaging);
    }
}
