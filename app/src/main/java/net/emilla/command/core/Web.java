package net.emilla.command.core;

import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.config.SettingVals;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.web.WebsiteMap;

/*internal*/ final class Web extends CoreCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_WEB_SEARCH))
            || Apps.canDo(pm, Intents.view("https:"))
            || Apps.canDo(pm, Intents.view("http:"));
    }

    private final WebsiteMap mWebsiteMap;

    /*internal*/ Web(AssistActivity act) {
        super(act, CoreEntry.WEB, EditorInfo.IME_ACTION_SEARCH);

        mWebsiteMap = new WebsiteMap(SettingVals.searchEngineCsv(act.prefs()));
    }

    @Override
    protected void run(AssistActivity act) {
        appSucceed(act, new Intent(ACTION_WEB_SEARCH));
    }

    @Override
    protected void run(AssistActivity act, String query) {
        // Todo: make a UI for this.
        //  - don't allow multiple %s's in the setting (i don't imagine there's any use for that right?)
        //    - handle character escapes if you want to be particular about it but the user can
        //      probably do that themself.
        appSucceed(act, mWebsiteMap.intent(query));
    }

}
