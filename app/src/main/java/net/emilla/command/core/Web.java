package net.emilla.command.core;

import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_WEB_SEARCH;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Web extends CoreCommand {

    public static final String ENTRY = "web";

    private static class WebParams extends CoreParams {

        private WebParams() {
            super(R.string.command_web,
                  R.string.instruction_web,
                  R.drawable.ic_web,
                  EditorInfo.IME_ACTION_SEARCH);
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
