package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Youtube extends AppSearch {

    public static final String PKG = "com.google.android.youtube";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_youtube;
    @StringRes
    public static final int SUMMARY = R.string.summary_video;

    public Youtube(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_video,
              R.string.summary_video);
    }
}
