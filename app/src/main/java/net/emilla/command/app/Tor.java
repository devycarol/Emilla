package net.emilla.command.app;

import static net.emilla.app.AppProperties.suppressiveFree;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppActions;
import net.emilla.app.AppProperties;

public final class Tor extends AppCommand {

    public static final String PKG = "org.torproject.torbrowser";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_tor;
    @StringRes
    private static final int SUMMARY = R.string.summary_web;

    public static AppProperties meta() {
        return suppressiveFree(ALIASES, SUMMARY, AppActions.FLAG_SEND | AppActions.FLAG_SEARCH);
        // search/send intents are broken.
    }

    public Tor(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
