package net.emilla.command.core;

import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.config.SettingVals;
import net.emilla.util.SearchEngineParser;

public final class Web extends CoreCommand {

    public static final String ENTRY = "web";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.WEB, true);
    }

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_WEB_SEARCH))
            || Apps.canDo(pm, Apps.viewTask("https:"))
            || Apps.canDo(pm, Apps.viewTask("http:"));
    }

    public static final String DFLT_SEARCH_ENGINES = """
            Wikipedia, wiki, w, https://wikipedia.org/wiki/%s
            Google, g, https://www.google.com/search?q=%s
            Google Images, gimages, gimage, gimg, gi, https://www.google.com/search?q=%s&udm=2
            YouTube, yt, y, https://www.youtube.com/results?search_query=%s
            DuckDuckGo, ddg, dd, d, https://duckduckgo.com/?q=%s
            DuckDuckGo Images, duckimages, duckimage, duckimg, ddgimages, ddgimage, ddgimg, ddgi, ddimages, ddimage, ddimg, ddi, dimages, dimage, dimg, https://duckduckgo.com/?q=%s&ia=images&iax=images""";

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
