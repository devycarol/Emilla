package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.date.Time;
import net.emilla.util.Apps;

final class Timer extends CoreDataCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, baseIntent());
    }

    @internal Timer(Context ctx) {
        super(ctx, CoreEntry.TIMER, R.string.data_hint_label);
    }

    private static Intent baseIntent() {
        return new Intent(AlarmClock.ACTION_SET_TIMER);
    }

    @Override
    protected void run(AssistActivity act) {
        Apps.succeed(act, baseIntent());
    }

    @Override
    protected void run(AssistActivity act, String duration) {
        runWithData(act, duration, null);
    }

    @Override
    public void runWithData(AssistActivity act, String title) {
        act.offer(a -> {});
    }

    @Override
    public void runWithData(AssistActivity act, String duration, @Nullable String title) {
        Integer durationSeconds = Time.parseDuration(duration, CoreEntry.TIMER.name);
        if (durationSeconds == null) {
            fail(act, R.string.error_invalid_duration);
            return;
        }

        Intent intent = baseIntent()
            .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            .putExtra(AlarmClock.EXTRA_LENGTH, durationSeconds.intValue())
        ;
        if (title != null) {
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, title);
        }
        Apps.succeed(act, intent);
    }
}
