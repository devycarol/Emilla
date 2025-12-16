package net.emilla.command.core;

import static android.provider.AlarmClock.ACTION_SET_TIMER;
import static android.provider.AlarmClock.EXTRA_LENGTH;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.provider.AlarmClock.EXTRA_SKIP_UI;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.date.Time;
import net.emilla.util.Apps;

final class Timer extends CoreDataCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeIntent());
    }

    /*internal*/ Timer(Context ctx) {
        super(ctx, CoreEntry.TIMER, R.string.data_hint_label);
    }

    private static Intent makeIntent() {
        return new Intent(ACTION_SET_TIMER);
    }

    private static Intent makeIntent(String duration) {
        int durationSeconds = Time.parseDuration(duration, CoreEntry.TIMER.name).seconds;
        return makeIntent()
            .putExtra(EXTRA_SKIP_UI, true)
            .putExtra(EXTRA_LENGTH, durationSeconds);
    }

    @Override
    protected void run(AssistActivity act) {
        appSucceed(act, makeIntent());
    }

    @Override
    protected void run(AssistActivity act, String duration) {
        appSucceed(act, makeIntent(duration));
    }

    @Override
    public void runWithData(AssistActivity act, String title) {
        throw badCommand(R.string.error_unfinished_timer_label);
        // TODO: is this always the case? I'd strongly prefer to defer to the user's app's UI. At
        //  the least, this failure should be replaced with an input dialog for com.android.deskclock
        //  in the *current* android version (previous ones may have been more functional, as we
        //  know...)
    }

    @Override
    public void runWithData(AssistActivity act, String duration, String title) {
        appSucceed(act, makeIntent(duration).putExtra(EXTRA_MESSAGE, title));
    }

}
