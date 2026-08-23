package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import net.emilla.Feedback;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.util.Apps;
import net.emilla.wadget.ActionSurface;

final class Dial extends EmillaCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_DIAL));
    }

    @internal Dial(ActionSurface surface) {
        super(surface, CoreEntry.DIAL, ImeAction.GO);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return Feedback.succeed(new Intent(ACTION_DIAL));
    }

    @Override
    protected Feedback run(ActionSurface surface, String numberOrPhoneword) {
        return Feedback.succeed(new Intent(ACTION_DIAL)
            .setData(Uri.parse("tel:" + numberOrPhoneword))
        );
    }
}
