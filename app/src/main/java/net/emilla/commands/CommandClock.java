package net.emilla.commands;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaBadCommandException;

import java.text.Format;

public class CommandClock extends CoreCommand {
public CommandClock(final AssistActivity act, final String instruct) {
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
    final String localTime;
    final Format time;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        final Calendar cal = Calendar.getInstance();
        time = DateFormat.getTimeInstance();
        localTime = string(R.string.toast_local_time, time.format(cal.getTime()));
    } else {
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        time = java.text.DateFormat.getTimeInstance();
        localTime = string(R.string.toast_local_time, time.format(cal.getTime()));
    }
    // todo: configurable format &/ allow adjustment via the command instruction
    give(localTime, false);
}

@Override
protected void run(final String location) {
    throw new EmlaBadCommandException("Sorry! No locations or elapse-time yet, working on it."); // TODO
}
}
