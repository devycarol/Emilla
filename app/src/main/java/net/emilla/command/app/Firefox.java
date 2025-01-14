package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Firefox extends AppSearch {

    public static final String PKG = "org.mozilla.firefox";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_firefox;

    private static class FirefoxParams extends AppSearchParams {

        private FirefoxParams(Yielder info) {
            super(info, R.string.instruction_web, R.string.summary_web);
        }
    }

    public Firefox(AssistActivity act, Yielder info) {
        super(act, new FirefoxParams(info));
    }
}
