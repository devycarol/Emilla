package net.emilla.command.core;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

import java.text.Format;

public final class Time extends CoreCommand {

    public static final String ENTRY = "time";
    @StringRes
    public static final int NAME = R.string.command_time;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_time;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Time::new, ENTRY, NAME, ALIASES);
    }

    private static final class TimeParams extends CoreParams {

        private TimeParams() {
            super(NAME,
                  R.string.instruction_location,
                  R.drawable.ic_clock,
                  EditorInfo.IME_ACTION_DONE,
                  R.string.summary_time,
                  R.string.manual_time);
        }
    }

    public Time(AssistActivity act) {
        super(act, new TimeParams());
    }

    @Override
    protected void run() {
        // Todo: show a widget dialog with copy button and other useful stuff
        // you could also show date information and call this command "date"
        String localTime;
        Format time;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            var cal = Calendar.getInstance();
            time = DateFormat.getTimeInstance();
            localTime = string(R.string.toast_local_time, time.format(cal.getTime()));
        } else {
            var cal = java.util.Calendar.getInstance();
            time = java.text.DateFormat.getTimeInstance();
            localTime = string(R.string.toast_local_time, time.format(cal.getTime()));
        }
        // todo: configurable format &/ allow adjustment via the command instruction
        giveText(localTime, false);
    }

    @Override
    protected void run(@NonNull String location) {
        throw badCommand(R.string.error_unfinished_time_location_elapse); // TODO
    }
}
