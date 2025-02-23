package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public final class Youtube extends AppSearch {

    public static final String PKG = "com.google.android.youtube";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_youtube;

    public Youtube(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_video,
              R.string.summary_video);
    }
}
