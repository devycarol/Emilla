package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public final class Firefox extends AppSearch {

    public static final String PKG = "org.mozilla.firefox";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_firefox;

    public Firefox(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_web,
              R.string.summary_web);
    }
}
