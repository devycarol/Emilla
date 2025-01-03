package net.emilla.command.core;

import static android.provider.AlarmClock.*;
import static java.lang.String.format;
import static java.util.Locale.ROOT;

import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaAppsException;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.utils.Time;

public class Timer extends CoreDataCommand {

    private static int seconds(Timer cmd, String duration) {
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
            cmd.toast(format(ROOT, "Warning! Timer set for %s, not %s.", endTime, curPeriod), true); // not good...
        }
        int offset = timeUnits.length == 6 ? 1 : 0; // remind me what this means??
        return timeUnits[0] * 3600 + timeUnits[1] * 60 + timeUnits[2] - offset;
    }

    private final Intent mIntent = new Intent(ACTION_SET_TIMER)
            .putExtra(EXTRA_SKIP_UI, true);
        private final Intent mUiIntent = new Intent(ACTION_SET_TIMER);

        @Override @ArrayRes
    public int detailsId() {
        return R.array.details_timer;
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_timer;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_timer;
    }

    public Timer(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_timer, R.string.instruction_timer);
    }

    @Override
    protected void run() {
        if (mUiIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        appSucceed(mUiIntent);
    }

    @Override
    protected void run(String duration) {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        appSucceed(mIntent.putExtra(EXTRA_LENGTH, seconds(this, duration)));
    }

    @Override
    protected void runWithData(String title) {
        throw new EmlaBadCommandException("Sorry! I can't label the timer without a duration.");
        // TODO: is this always the case? I'd strongly prefer to defer to the user's app's UI. At
        //  the least, this failure should be replaced with an input dialog for com.android.deskclock
        //  in the *current* android version (previous ones may have been more functional, as we
        //  know...)
    }

    @Override
    protected void runWithData(String duration, String title) {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        appSucceed(mIntent.putExtra(EXTRA_LENGTH, seconds(this, duration)).putExtra(EXTRA_MESSAGE, title));
    }
}
