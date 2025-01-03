package net.emilla.command.core;

import static android.provider.AlarmClock.*;
import static java.lang.Float.parseFloat;
import static java.util.regex.Pattern.compile;

import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaAppsException;
import net.emilla.exception.EmlaBadCommandException;

import java.util.regex.Matcher;

public class Pomodoro extends CoreDataCommand {

    private final Intent mIntent = new Intent(ACTION_SET_TIMER)
            .putExtra(EXTRA_SKIP_UI, true)
            .putExtra(EXTRA_LENGTH, 1500 /*25m*/); // todo: make configurable

    @Override @ArrayRes
    public int detailsId() {
        return R.array.details_pomodoro;
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_pomodoro;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_pomodoro;
    }

    public Pomodoro(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_pomodoro, R.string.instruction_pomodoro);
        mIntent.putExtra(EXTRA_MESSAGE, string(R.string.command_pomodoro));
    }

    /**
     * @return true if this is a break timer
     */
    private boolean putDuration(Resources res, String duration) throws EmlaBadCommandException {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        Matcher m = compile(" *b(reak)? *").matcher(duration); // TODO LANG
        boolean isBreak = m.find();
        if (isBreak) duration = m.replaceFirst("");

        if (duration.isEmpty()) mIntent.putExtra(EXTRA_LENGTH, 300 /*5m*/); // todo: make configurable
        // only reached if the string was emptied by the stripping the 'break' tag
        else try {
            float dur = parseFloat(duration);
            // todo: I need to learn more about float errors..
            //  and this function... ion wanna worry about hexadecimal :sob:
            if (dur <= 0.0f) throw new EmlaBadCommandException(
                    res.getString(R.string.error_bad_minutes, duration));
            mIntent.putExtra(EXTRA_LENGTH, (int) (dur * 60.0f));
        } catch (NumberFormatException e) {
            throw new EmlaBadCommandException(res.getString(R.string.error_bad_minutes, duration));
        }
        return isBreak;
    }

    @Override
    protected void run() {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        appSucceed(mIntent);
    }

    @Override
    protected void run(String duration) throws EmlaBadCommandException {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        boolean isBreak = putDuration(resources, duration);
        if (isBreak) mIntent.putExtra(EXTRA_MESSAGE, string(R.string.memo_pomodoro_break));
        appSucceed(mIntent);
        toast(isBreak ? string(R.string.toast_pomodoro_break)
                : string(R.string.toast_pomodoro), false);
    }

    @Override
    protected void runWithData(String memo) {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        appSucceed(mIntent.putExtra(EXTRA_MESSAGE, memo));
    }

    @Override
    protected void runWithData(String duration, String memo) throws EmlaBadCommandException {
        if (mIntent.resolveActivity(pm) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
        boolean isBreak = putDuration(resources, duration);
        mIntent.putExtra(EXTRA_MESSAGE, memo);
        appSucceed(mIntent);
        toast(isBreak ? string(R.string.toast_pomodoro_break)
                : string(R.string.toast_pomodoro), false);
    }
}
