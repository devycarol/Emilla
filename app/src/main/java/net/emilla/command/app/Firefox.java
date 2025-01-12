package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Firefox extends AppSearch {

    public static final String PKG = "org.mozilla.firefox";

    private static class FirefoxParams extends AppSearchParams {

        private FirefoxParams(AppInfo info) {
            super(info, R.string.instruction_web, R.string.summary_web);
        }
    }

    public Firefox(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new FirefoxParams(info));
    }
}
