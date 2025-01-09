package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Youtube extends AppSearch {

    public static final String PKG = "com.google.android.youtube";

    private static class YoutubeParams extends AppSearchParams {

        private YoutubeParams(AppInfo info) {
            super(info, R.string.instruction_video);
        }
    }

    public Youtube(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new YoutubeParams(info));
    }
}
