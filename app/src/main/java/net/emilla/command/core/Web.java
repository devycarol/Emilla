package net.emilla.command.core;

import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.web.WebsiteMap;

final class Web extends CoreCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_WEB_SEARCH))
            || Apps.canDo(pm, Intents.view("https:"))
            || Apps.canDo(pm, Intents.view("http:"));
    }

    private final WebsiteMap mWebsiteMap;

    /*internal*/ Web(AssistActivity act) {
        super(act, CoreEntry.WEB, EditorInfo.IME_ACTION_SEARCH);

        var prefs = act.getSharedPreferences();
        var res = act.getResources();
        mWebsiteMap = new WebsiteMap(prefs, res);
    }

    @Override
    protected void run(AssistActivity act) {
        appSucceed(act, new Intent(ACTION_WEB_SEARCH));
    }

    @Override
    protected void run(AssistActivity act, String query) {
        appSucceed(act, mWebsiteMap.intent(query));
    }

}
