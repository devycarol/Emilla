package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

final class Navigate extends EmillaCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeFilter());
    }

    @internal Navigate(ActionSurface surface) {
        super(surface, CoreEntry.NAVIGATE, ImeAction.SEARCH);
    }

    private static Intent makeFilter() {
        return Intents.view("geo:");
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return CategoryCommand.run(surface, makeFilter());
    }

    @Override
    protected Feedback run(ActionSurface surface, String location) {
        // Todo: location bookmarks, navigate to contacts' addresses
        return Feedback.succeed(Intents.view(Uri.parse("geo:0,0?q=" + location)));
    }
}
