package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Signal extends MultilineMessenger {

    public static final String PKG = "org.thoughtcrime.securesms";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_signal;

    public Signal(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
