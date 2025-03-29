package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Tubular extends VideoSearchBySend {

    public static final String PKG = "org.polymorphicshade.tubular";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_tubular;
    @StringRes
    public static final int SUMMARY = R.string.summary_video;

    public Tubular(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
