package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

final class Navigate extends CategoryCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.view("geo:"));
    }

    /*internal*/ Navigate(AssistActivity act) {
        super(act, CoreEntry.NAVIGATE, EditorInfo.IME_ACTION_SEARCH);
    }

    @Override
    protected Intent makeFilter() {
        return Intents.view("geo:");
    }

    @Override
    protected void run(AssistActivity act, String location) {
        // Todo: location bookmarks, navigate to contacts' addresses
        appSucceed(act, Intents.view(Uri.parse("geo:0,0?q=" + location)));
    }

}
