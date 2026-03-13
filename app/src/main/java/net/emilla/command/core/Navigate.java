package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

final class Navigate extends CoreCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeFilter());
    }

    @internal Navigate(Context ctx) {
        super(ctx, CoreEntry.NAVIGATE, EditorInfo.IME_ACTION_SEARCH);
    }

    private static Intent makeFilter() {
        return Intents.view("geo:");
    }

    @Override
    protected void run(AssistActivity act) {
        CategoryCommand.run(act, makeFilter());
    }

    @Override
    protected void run(AssistActivity act, String location) {
        // Todo: location bookmarks, navigate to contacts' addresses
        Apps.succeed(act, Intents.view(Uri.parse("geo:0,0?q=" + location)));
    }
}
