package net.emilla.command.core;

import static android.provider.AlarmClock.ACTION_SET_TIMER;
import static android.provider.AlarmClock.EXTRA_LENGTH;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.provider.AlarmClock.EXTRA_SKIP_UI;
import static java.lang.Float.parseFloat;

import android.content.Intent;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

import java.util.regex.Pattern;

public class Pomodoro extends CoreDataCommand {

    public static final String ENTRY = "pomodoro";
    @StringRes
    public static final int NAME = R.string.command_pomodoro;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_pomodoro;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Pomodoro::new, ENTRY, NAME, ALIASES);
    }

    private static class PomodoroParams extends CoreDataParams {

        private PomodoroParams() {
            super(NAME,
                  R.string.instruction_pomodoro,
                  R.drawable.ic_pomodoro,
                  R.string.summary_pomodoro,
                  R.string.manual_pomodoro,
                  R.string.data_hint_pomodoro);
        }
    }

    private final Intent mIntent = new Intent(ACTION_SET_TIMER)
            .putExtra(EXTRA_SKIP_UI, true)
            .putExtra(EXTRA_LENGTH, 1500 /*25m*/); // todo: make configurable

    public Pomodoro(AssistActivity act) {
        super(act, new PomodoroParams());
        mIntent.putExtra(EXTRA_MESSAGE, string(NAME));
    }

    /**
     * @return true if this is a break timer
     */
    private boolean putDuration(String duration) throws EmlaBadCommandException {
        final var m = Pattern.compile(" *b(reak)? *").matcher(duration); // TODO LANG
        boolean isBreak = m.find();
        if (isBreak) duration = m.replaceFirst("");

        if (duration.isEmpty()) mIntent.putExtra(EXTRA_LENGTH, 300 /*5m*/); // todo: make configurable
        // only reached if the string was emptied by the stripping the 'break' tag
        else try {
            float dur = parseFloat(duration);
            // todo: I need to learn more about float errors..
            //  and this function... ion wanna worry about hexadecimal :sob:
            if (dur <= 0.0f) throw badCommand(R.string.error_bad_minutes);
            mIntent.putExtra(EXTRA_LENGTH, (int) (dur * 60.0f));
        } catch (NumberFormatException e) {
            throw badCommand(R.string.error_bad_minutes);
        }
        return isBreak;
    }

    @Override
    protected void run() {
        appSucceed(mIntent);
    }

    @Override
    protected void run(@NonNull String duration) throws EmlaBadCommandException {
        boolean isBreak = putDuration(duration);
        if (isBreak) mIntent.putExtra(EXTRA_MESSAGE, string(R.string.memo_pomodoro_break));
        appSucceed(mIntent);
        toast(isBreak ? string(R.string.toast_pomodoro_break)
                : string(R.string.toast_pomodoro));
    }

    @Override
    protected void runWithData(@NonNull String memo) {
        appSucceed(mIntent.putExtra(EXTRA_MESSAGE, memo));
    }

    @Override
    protected void runWithData(@NonNull String duration, @NonNull String memo) throws EmlaBadCommandException {
        boolean isBreak = putDuration(duration);
        mIntent.putExtra(EXTRA_MESSAGE, memo);
        appSucceed(mIntent);
        toast(isBreak ? string(R.string.toast_pomodoro_break)
                : string(R.string.toast_pomodoro));
    }
}
