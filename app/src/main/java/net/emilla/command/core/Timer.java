package net.emilla.command.core;

import static android.provider.AlarmClock.ACTION_SET_TIMER;
import static android.provider.AlarmClock.EXTRA_LENGTH;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.provider.AlarmClock.EXTRA_SKIP_UI;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;
import net.emilla.lang.date.Time;

public final class Timer extends CoreDataCommand {

    public static final String ENTRY = "timer";
    @StringRes
    public static final int NAME = R.string.command_timer;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_timer;

    public static Yielder yielder() {
        return new Yielder(true, Timer::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, makeIntent());
    }

    private Timer(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_timer,
              R.drawable.ic_timer,
              R.string.summary_timer,
              R.string.manual_timer,
              R.string.data_hint_label);
    }

    private static Intent makeIntent() {
        return new Intent(ACTION_SET_TIMER);
    }

    private static Intent makeIntent(String duration) {
        int durationSeconds = Time.parseDuration(duration, NAME).seconds;
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
