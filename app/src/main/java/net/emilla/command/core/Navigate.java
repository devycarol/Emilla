package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;

public final class Navigate extends CategoryCommand {

    public static final String ENTRY = "navigate";
    @StringRes
    public static final int NAME = R.string.command_navigate;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_navigate;

    public static Yielder yielder() {
        return new Yielder(true, Navigate::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Apps.viewTask("geo:"));
    }

    private Navigate(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_location,
              R.drawable.ic_navigate,
              R.string.summary_navigate,
              R.string.manual_navigate,
              EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected Intent makeFilter() {
        return Apps.viewTask("geo:");
    }

    @Override
    protected void run(String location) {
        // Todo: location bookmarks, navigate to contacts' addresses
        appSucceed(Apps.viewTask(Uri.parse("geo:0,0?q=" + location)));
    }
}
