package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.command.ImeAction;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.lang.Lang;
import net.emilla.util.Apps;
import net.emilla.util.Int;
import net.emilla.wadget.ActionSurface;

final class Timer extends EmillaCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, baseIntent());
    }

    @internal Timer(ActionSurface surface) {
        super(surface, CoreEntry.TIMER, ImeAction.DO);
    }

    @Override
    protected DataDirective dataDirective() {
        return new DataField(R.string.data_hint_label);
    }

    private static Intent baseIntent() {
        return new Intent(AlarmClock.ACTION_SET_TIMER);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return surface.dataText() != null
            ? Feedback.succeed(baseIntent())
            : Feedback.pend()
        ;
    }

    @Override
    protected Feedback run(ActionSurface surface, String duration) {
        String title = surface.dataText();
        Int box = Lang.durationSeconds(surface.getContext(), duration);
        if (box == null) {
            return Feedback.fail(R.string.error_invalid_duration);
        }

        int seconds = box.intValue();
        Intent intent = baseIntent()
            .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
        ;
        if (title != null) {
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, title);
        }
        return Feedback.succeed(intent);
    }
}
