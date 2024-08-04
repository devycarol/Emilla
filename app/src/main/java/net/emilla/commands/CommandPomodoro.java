package net.emilla.commands;

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
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;

import java.util.regex.Matcher;

public
class CommandPomodoro extends CoreDataCommand {
private final Intent mIntent = new Intent(ACTION_SET_TIMER)
        .putExtra(EXTRA_SKIP_UI, true)
        .putExtra(EXTRA_LENGTH, 1500 /*25m*/); // todo: make configurable

@Override
public Command cmd() {
    return Command.POMODORO;
}

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

public CommandPomodoro(final AssistActivity act) {
    super(act, R.string.command_pomodoro, R.string.instruction_pomodoro);
    mIntent.putExtra(EXTRA_MESSAGE, resources().getString(R.string.command_pomodoro));
}

/**
 * @return true if this is a break timer
 */
private boolean putDuration(final Resources res, /*mutable*/ String duration) throws EmlaBadCommandException {
    if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
    final Matcher m = compile(" *b(reak)? *").matcher(duration); // TODO: lang
    final boolean isBreak = m.find();
    if (isBreak) duration = m.replaceFirst("");

    if (duration.isEmpty()) mIntent.putExtra(EXTRA_LENGTH, 300 /*5m*/); // todo: make configurable
    // only reached if the string was emptied by the stripping the 'break' tag
    else try {
        final float dur = parseFloat(duration);
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
public void run() {
    if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
    succeed(mIntent);
}

@Override
public void run(final String duration) throws EmlaBadCommandException {
    if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
    final Resources res = resources();
    final boolean isBreak = putDuration(res, duration);
    if (isBreak) mIntent.putExtra(EXTRA_MESSAGE, res.getString(R.string.memo_pomodoro_break));
    succeed(mIntent);
    toast(isBreak ? res.getString(R.string.toast_pomodoro_break)
            : res.getString(R.string.toast_pomodoro), false);
}

@Override
public void runWithData(final String memo) {
    if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
    succeed(mIntent.putExtra(EXTRA_MESSAGE, memo));
}

@Override
public void runWithData(final String duration, final String memo) throws EmlaBadCommandException {
    if (mIntent.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No timer app found on your device."); // todo handle at mapping
    final Resources res = resources();
    final boolean isBreak = putDuration(res, duration);
    mIntent.putExtra(EXTRA_MESSAGE, memo);
    succeed(mIntent);
    toast(isBreak ? res.getString(R.string.toast_pomodoro_break)
            : res.getString(R.string.toast_pomodoro), false);
}
}
