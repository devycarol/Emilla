package net.emilla.command.app;

import static net.emilla.app.AppProperties.suppressiveFree;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppActions;
import net.emilla.app.AppProperties;

public final class Firefox extends AppSearch {

    public static final String PKG = "org.mozilla.firefox";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_firefox;
    @StringRes
    private static final int SUMMARY = R.string.summary_web;

    public static AppProperties meta() {
        return suppressiveFree(ALIASES, SUMMARY, AppActions.FLAG_SEND);
        // 'send' is redundant for Firefox, it just searches.
    }

    public Firefox(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_web,
              SUMMARY);
    }
}
