package net.emilla.command.core;

import static android.provider.AlarmClock.ACTION_SET_TIMER;
import static android.provider.AlarmClock.EXTRA_LENGTH;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.provider.AlarmClock.EXTRA_SKIP_UI;

import android.content.Intent;
import android.content.pm.PackageManager;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.date.Time;
import net.emilla.util.Apps;

/*internal*/ final class Timer extends CoreDataCommand {

    public static final String ENTRY = "timer";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeIntent());
    }

    /*internal*/ Timer(AssistActivity act) {
        super(act, CoreEntry.TIMER, R.string.data_hint_label);
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
    protected void run() {
        appSucceed(makeIntent());
    }

    @Override
    protected void run(String duration) {
        appSucceed(makeIntent(duration));
    }

    @Override
    protected void runWithData(String title) {
        throw badCommand(R.string.error_unfinished_timer_label);
        // TODO: is this always the case? I'd strongly prefer to defer to the user's app's UI. At
        //  the least, this failure should be replaced with an input dialog for com.android.deskclock
        //  in the *current* android version (previous ones may have been more functional, as we
        //  know...)
    }

    @Override
    protected void runWithData(String duration, String title) {
        appSucceed(makeIntent(duration).putExtra(EXTRA_MESSAGE, title));
    }

}
