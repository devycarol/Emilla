package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppProperties;

public final class Newpipe extends VideoSearchBySend {

    public static final String PKG = "org.schabi.newpipe";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_newpipe;
    @StringRes
    private static final int SUMMARY = R.string.summary_video;

    public static AppProperties meta() {
        return AppProperties.ordinaryFree(ALIASES, SUMMARY);
    }

    public Newpipe(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
