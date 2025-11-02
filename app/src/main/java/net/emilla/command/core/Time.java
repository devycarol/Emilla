package net.emilla.command.core;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

import java.text.Format;

public final class Time extends CoreCommand {

    public static final String ENTRY = "time";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.TIME, true);
    }

    public static boolean possible() {
        return true;
    }

    /*internal*/ Time(AssistActivity act) {
        super(act, CoreEntry.TIME, EditorInfo.IME_ACTION_DONE);
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
            localTime = str(R.string.toast_local_time, time.format(cal.getTime()));
        } else {
            var cal = java.util.Calendar.getInstance();
            time = java.text.DateFormat.getTimeInstance();
            localTime = str(R.string.toast_local_time, time.format(cal.getTime()));
        }
        // todo: configurable format &/ allow adjustment via the command instruction
        giveText(localTime);
    }

    @Override
    protected void run(String location) {
        throw badCommand(R.string.error_unfinished_time_location_elapse); // TODO
    }
}
