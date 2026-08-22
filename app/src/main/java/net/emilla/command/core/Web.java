package net.emilla.command.core;

import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.wadget.ActionSurface;
import net.emilla.web.WebsiteMap;

final class Web extends EmillaCommand {
    public static boolean possible() {
        return true;
    }

    private final WebsiteMap mWebsiteMap;

    @internal Web(AssistActivity act) {
        super(act, CoreEntry.WEB, EditorInfo.IME_ACTION_SEARCH);

        var prefs = act.getSharedPreferences();
        var res = act.getResources();
        mWebsiteMap = new WebsiteMap(prefs, res);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return Feedback.succeed(new Intent(ACTION_WEB_SEARCH));
    }

    @Override
    protected Feedback run(ActionSurface surface, String query) {
        return Feedback.succeed(mWebsiteMap.intent(query));
    }
}
