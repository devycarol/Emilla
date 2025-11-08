package net.emilla.command.core;

import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.config.SettingVals;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.util.SearchEngineParser;

/*internal*/ final class Web extends CoreCommand {

    public static final String ENTRY = "web";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_WEB_SEARCH))
            || Apps.canDo(pm, Intents.view("https:"))
            || Apps.canDo(pm, Intents.view("http:"));
    }

    private SearchEngineParser mSearchEngineMap = null;

    /*internal*/ Web(AssistActivity act) {
        super(act, CoreEntry.WEB, EditorInfo.IME_ACTION_SEARCH);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mSearchEngineMap == null) {
            mSearchEngineMap = new SearchEngineParser(SettingVals.searchEngineCsv(prefs()));
        }
    }

    @Override
    protected void onClean() {
        super.onClean();
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_WEB_SEARCH));
    }

    @Override
    protected void run(String query) {
        // Todo: make a UI for this.
        //  - don't allow multiple %s's in the setting (i don't imagine there's any use for that right?)
        //    - handle character escapes if you want to be particular about it but the user can
        //      probably do that themself.
        appSucceed(mSearchEngineMap.intent(query));
    }

}
