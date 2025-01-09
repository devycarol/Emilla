package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_MAPS;

import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;

public class CateNavigate extends CategoryCommand {

    public static final String ENTRY = "navigate";

    private static class NavigateParams extends CoreParams {

        private NavigateParams() {
            super(R.string.command_navigate,
                  R.string.instruction_location,
                  R.drawable.ic_navigate,
                  EditorInfo.IME_ACTION_GO);
        }
    }

    public CateNavigate(AssistActivity act, String instruct) {
        super(act, instruct, new NavigateParams(), CATEGORY_APP_MAPS);
    }

    @Override
    protected void run(String location) {
        // Todo: location bookmarks, navigate to contacts' addresses
        appSucceed(Apps.viewTask(Uri.parse("geo:0,0?q=" + location)));
    }
}
