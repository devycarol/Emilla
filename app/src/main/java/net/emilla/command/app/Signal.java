package net.emilla.command.app;

import net.emilla.AssistActivity;

public class Signal extends MultilineMessenger {

    public static final String PKG = "org.thoughtcrime.securesms";

    public Signal(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, info);
    }
}
