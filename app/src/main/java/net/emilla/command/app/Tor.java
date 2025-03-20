package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Tor extends AppCommand { // search/send intents are broken.

    public static final String PKG = "org.torproject.torbrowser";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_tor;

    public Tor(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
