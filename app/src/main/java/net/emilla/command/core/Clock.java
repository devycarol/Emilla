package net.emilla.command.core;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

import java.text.Format;

public class Clock extends CoreCommand {

    public static final String ENTRY = "clock";

    public Clock(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_clock, R.string.instruction_location);
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_clock;
    }

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_DONE;
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
        throw new EmlaBadCommandException(R.string.command_clock, R.string.error_unfinished_clock_location_elapse); // TODO
    }
}
