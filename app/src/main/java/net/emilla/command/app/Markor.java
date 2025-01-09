package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Markor extends AppSendData {

    public static final String PKG = "net.gsantner.markor";
    public static final String CLS_MAIN = Markor.PKG + ".activity.MainActivity";

    public static AppCommand instance(AssistActivity act, String instruct, AppInfo info) {
        return info.cls.equals(CLS_MAIN) ? new Markor(act, instruct, info)
                : new AppCommand(act, instruct, info);
        // Markor can have multiple launchers, only the main should have the 'send' property.
    }

    private static class MarkorParams extends AppSendDataParams {

        private MarkorParams(AppInfo info) {
            super(info, R.string.instruction_text, R.string.data_hint_note);
        }
    }

    private Markor(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new MarkorParams(info));
    }
}
