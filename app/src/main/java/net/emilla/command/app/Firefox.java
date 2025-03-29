package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Firefox extends AppSearch {

    public static final String PKG = "org.mozilla.firefox";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_firefox;
    @StringRes
    public static final int SUMMARY = R.string.summary_web;

    public Firefox(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_web,
              SUMMARY);
    }
}
