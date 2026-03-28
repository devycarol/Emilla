package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.AlarmClock;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.date.Time;
import net.emilla.util.Apps;

final class Timer extends CoreDataCommand {
    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeIntent());
    }

    @internal Timer(Context ctx) {
        super(ctx, CoreEntry.TIMER, R.string.data_hint_label);
    }

    private static Intent makeIntent() {
        return new Intent(AlarmClock.ACTION_SET_TIMER);
    }

    private static Intent makeIntent(String duration) {
        int durationSeconds = Time.parseDuration(duration, CoreEntry.TIMER.name).seconds;
        return makeIntent()
            .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            .putExtra(AlarmClock.EXTRA_LENGTH, durationSeconds)
        ;
    }

    @Override
    protected void run(AssistActivity act) {
        Apps.succeed(act, makeIntent());
    }

    @Override
    protected void run(AssistActivity act, String duration) {
        Apps.succeed(act, makeIntent(duration));
    }

    @Override
    public void runWithData(AssistActivity act, String title) {
        act.offer(a -> {});
    }

    @Override
    public void runWithData(AssistActivity act, String duration, String title) {
        Apps.succeed(act, makeIntent(duration).putExtra(AlarmClock.EXTRA_MESSAGE, title));
    }
}
