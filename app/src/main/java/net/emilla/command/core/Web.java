package net.emilla.command.core;

import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.settings.SettingVals;
import net.emilla.util.SearchEngineParser;

public class Web extends CoreCommand {

    public static final String ENTRY = "web";
    @StringRes
    public static final int NAME = R.string.command_web;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_web;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Web::new, ENTRY, NAME, ALIASES);
    }

    public static final String DFLT_SEARCH_ENGINES = """
            Wikipedia, wiki, w, https://wikipedia.org/wiki/%s
            Google, g, https://www.google.com/search?q=%s
            Google Images, gimages, gimage, gimg, gi, https://www.google.com/search?q=%s&udm=2
            YouTube, yt, y, https://www.youtube.com/results?search_query=%s
            DuckDuckGo, ddg, dd, d, https://duckduckgo.com/?q=%s
            DuckDuckGo Images, duckimages, duckimage, duckimg, ddgimages, ddgimage, ddgimg, ddgi, ddimages, ddimage, ddimg, ddi, dimages, dimage, dimg, https://duckduckgo.com/?q=%s&ia=images&iax=images""";

    private static class WebParams extends CoreParams {

        private WebParams() {
            super(NAME,
                  R.string.instruction_web,
                  R.drawable.ic_web,
                  EditorInfo.IME_ACTION_SEARCH,
                  R.string.summary_web,
                  R.string.manual_web);
        }
    }

    private SearchEngineParser mSearchEngineMap;

    public Web(AssistActivity act) {
        super(act, new WebParams());
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
