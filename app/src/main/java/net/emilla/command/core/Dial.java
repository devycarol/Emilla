package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.util.Apps;
import net.emilla.wadget.ActionSurface;

final class Dial extends EmillaCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_DIAL));
    }

    @internal Dial(Context ctx) {
        super(ctx, CoreEntry.DIAL, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        return Feedback.succeed(new Intent(ACTION_DIAL));
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String numberOrPhoneword) {
        return Feedback.succeed(new Intent(ACTION_DIAL)
            .setData(Uri.parse("tel:" + numberOrPhoneword))
        );
    }
}
