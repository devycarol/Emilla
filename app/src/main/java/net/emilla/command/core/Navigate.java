package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;

/*internal*/ final class Navigate extends CategoryCommand {

    public static final String ENTRY = "navigate";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Apps.viewTask("geo:"));
    }

    /*internal*/ Navigate(AssistActivity act) {
        super(act, CoreEntry.NAVIGATE, EditorInfo.IME_ACTION_SEARCH);
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
