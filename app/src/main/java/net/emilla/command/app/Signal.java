package net.emilla.command.app;

import static net.emilla.app.AppProperties.ordinaryFree;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppProperties;

public final class Signal extends MultilineMessenger {

    public static final String PKG = "org.thoughtcrime.securesms";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_signal;
    @StringRes
    private static final int SUMMARY = R.string.summary_messaging;

    public static AppProperties meta() {
        return ordinaryFree(ALIASES, SUMMARY);
    }

    public Signal(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
