package net.emilla.command.core;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

import java.text.Format;

public class Time extends CoreCommand {

    public static final String ENTRY = "time";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_time;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class TimeParams extends CoreParams {

        private TimeParams() {
            super(R.string.command_time,
                  R.string.instruction_location,
                  R.drawable.ic_clock,
                  EditorInfo.IME_ACTION_DONE);
        }
    }

    public Time(AssistActivity act, String instruct) {
        super(act, instruct, new TimeParams());
    }

    @Override
    protected void run() {
        // TODO: show a widget dialog with copy button and other useful stuff
        // you could also show date information and call this command "date"
        String localTime;
        Format time;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar cal = Calendar.getInstance();
            time = DateFormat.getTimeInstance();
            localTime = string(R.string.toast_local_time, time.format(cal.getTime()));
        } else {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            time = java.text.DateFormat.getTimeInstance();
            localTime = string(R.string.toast_local_time, time.format(cal.getTime()));
        }
        // todo: configurable format &/ allow adjustment via the command instruction
        giveText(localTime, false);
    }

    @Override
    protected void run(String location) {
        throw new EmlaBadCommandException(R.string.command_time, R.string.error_unfinished_time_location_elapse); // TODO
    }
}
