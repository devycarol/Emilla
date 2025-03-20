package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Newpipe extends VideoSearchBySend {

    public static final String PKG = "org.schabi.newpipe";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_newpipe;

    public Newpipe(AssistActivity act, Yielder info) {
        super(act, info);
    }
}
