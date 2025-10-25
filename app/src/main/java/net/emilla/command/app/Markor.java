package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppProperties;

public final class Markor extends AppSendData {

    public static final String PKG = "net.gsantner.markor";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_markor;
    @StringRes
    private static final int SUMMARY = R.string.summary_note;

    private static final String CLS_MAIN = PKG + ".activity.MainActivity";

    public static AppProperties meta(String cls) {
        return cls.equals(CLS_MAIN) ? AppProperties.ordinaryFree(ALIASES, SUMMARY) : AppProperties.suppressiveFree();
    }

    public static AppCommand instance(AssistActivity act, Yielder info, String cls) {
        return cls.equals(CLS_MAIN) ? new Markor(act, info) : new AppCommand(act, info);
        // Markor can have multiple launchers, only the main should have the 'send' property.
    }

    private Markor(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_text,
              SUMMARY,
              R.string.data_hint_note);
    }
}
