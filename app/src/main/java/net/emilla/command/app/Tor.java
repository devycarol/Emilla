package net.emilla.command.app;

import net.emilla.AssistActivity;

public class Tor extends AppCommand { // search/send intents are broken.

    public static final String PKG = "org.torproject.torbrowser";

    public Tor(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, info);
    }
}
