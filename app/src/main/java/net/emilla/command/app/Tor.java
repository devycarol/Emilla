package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Tor extends AppCommand { // search/send intents are broken.

    public static final String PKG = "org.torproject.torbrowser";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_tor;
    @StringRes
    public static final int SUMMARY = R.string.summary_web;

    public Tor(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
