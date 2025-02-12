package net.emilla.command.core;

import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;

public final class Navigate extends CategoryCommand {

    public static final String ENTRY = "navigate";
    @StringRes
    public static final int NAME = R.string.command_navigate;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_navigate;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Navigate::new, ENTRY, NAME, ALIASES);
    }

    private static final class NavigateParams extends CoreParams {

        private NavigateParams() {
            super(NAME,
                  R.string.instruction_location,
                  R.drawable.ic_navigate,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_navigate,
                  R.string.manual_navigate);
        }
    }

    public Navigate(AssistActivity act) {
        super(act, new NavigateParams());
    }

    @Override
    protected Intent makeFilter() {
        return Apps.viewTask(Uri.parse("geo:"));
    }

    @Override
    protected void run(@NonNull String location) {
        // Todo: location bookmarks, navigate to contacts' addresses
        appSucceed(Apps.viewTask(Uri.parse("geo:0,0?q=" + location)));
    }
}
