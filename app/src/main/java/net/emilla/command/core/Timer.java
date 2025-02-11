package net.emilla.command.core;

import static android.provider.AlarmClock.*;
import static java.lang.String.format;
import static java.util.Locale.ROOT;

import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;
import net.emilla.util.Time;

public class Timer extends CoreDataCommand {

    public static final String ENTRY = "timer";
    @StringRes
    public static final int NAME = R.string.command_timer;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_timer;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Timer::new, ENTRY, NAME, ALIASES);
    }

    private static class TimerParams extends CoreDataParams {

        private TimerParams() {
            super(NAME,
                  R.string.instruction_timer,
                  R.drawable.ic_timer,
                  R.string.summary_timer,
                  R.string.manual_timer,
                  R.string.data_hint_label);
        }
    }

    public Timer(AssistActivity act) {
        super(act, new TimerParams());
    }

    private Intent makeIntent() {
        return new Intent(ACTION_SET_TIMER);
    }

    private Intent makeIntent(String duration) {
        return makeIntent()
                .putExtra(EXTRA_SKIP_UI, true)
                .putExtra(EXTRA_LENGTH, seconds(duration));
    }

    private int seconds(String duration) {
        // todo: cleanup this logic
        int[] timeUnits = Time.parseDuration(duration);
        int warn = timeUnits[3];
        if (warn > 0) { // todo: replace with a confirm/set-default dialog - reduces localization woes
            String curPeriod, nextPeriod;
            if (warn == 1) {
                nextPeriod = "AM";
                curPeriod = "PM";
            } else {
                nextPeriod = "PM";
                curPeriod = "AM";
            }
            String endTime = format(ROOT, "%d:%02d%s", timeUnits[4], timeUnits[5], nextPeriod);
            toast(format(ROOT, "Warning! Timer set for %s, not %s.", endTime, curPeriod)); // not good...
        }
        int offset = timeUnits.length == 6 ? 1 : 0; // remind me what this means??
        return timeUnits[0] * 60 * 60 + timeUnits[1] * 60 + timeUnits[2] - offset;
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
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_timer_label);
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
