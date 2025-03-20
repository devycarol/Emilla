package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Markor extends AppSendData {

    public static final String PKG = "net.gsantner.markor";
    public static final String CLS_MAIN = PKG + ".activity.MainActivity";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_markor;

    public static AppCommand instance(AssistActivity act, Yielder info) {
        return info.cls.equals(CLS_MAIN) ? new Markor(act, info)
                : new AppCommand(act, info);
        // Markor can have multiple launchers, only the main should have the 'send' property.
    }

    private Markor(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_text,
              R.string.summary_note,
              R.string.data_hint_note);
    }
}
