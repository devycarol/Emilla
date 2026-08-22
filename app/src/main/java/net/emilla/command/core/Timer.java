package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import androidx.annotation.Nullable;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.util.Apps;
import net.emilla.util.Int;
import net.emilla.wadget.ActionSurface;

final class Timer extends CoreDataCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, baseIntent());
    }

    @internal Timer(ActionSurface surface) {
        super(surface, CoreEntry.TIMER, R.string.data_hint_label);
    }

    private static Intent baseIntent() {
        return new Intent(AlarmClock.ACTION_SET_TIMER);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        return Feedback.succeed(baseIntent());
    }

    @Override
    protected Feedback run(ActionSurface surface, String duration) {
        return runWithData(surface, duration, null);
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String title) {
        return Feedback.pend();
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String duration, @Nullable String title) {
        Int box = Lang.durationSeconds(surface.getAssistActivity(), duration);
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
