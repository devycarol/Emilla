package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.AppProperties;

public final class Tubular extends VideoSearchBySend {

    public static final String PKG = "org.polymorphicshade.tubular";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_tubular;
    @StringRes
    private static final int SUMMARY = R.string.summary_video;

    public static AppProperties meta() {
        return AppProperties.ordinaryFree(ALIASES, SUMMARY);
    }

    public Tubular(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
