package net.emilla.command.core;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

public class Web extends CoreCommand {

    public static final String ENTRY = "web";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_web;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class WebParams extends CoreParams {

        private WebParams() {
            super(R.string.command_web,
                  R.string.instruction_web,
                  R.drawable.ic_web,
                  EditorInfo.IME_ACTION_SEARCH,
                  R.string.summary_web,
                  R.string.manual_web);
        }
    }

    public Web(AssistActivity act, String instruct) {
        super(act, instruct, new WebParams());
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_WEB_SEARCH));
    }

    @Override
    protected void run(String searchOrUrl) {
        appSucceed(new Intent(ACTION_WEB_SEARCH).putExtra(QUERY, searchOrUrl));
    }
}
