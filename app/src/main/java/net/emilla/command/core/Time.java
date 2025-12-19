package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.run.TextGift;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

final class Time extends CoreCommand {

    @internal Time(Context ctx) {
        super(ctx, CoreEntry.TIME, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        var timeNow = LocalTime.now();
        var formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        // todo: also show date info
        // todo: configurable format
        act.give(new TextGift(act, R.string.local_time, timeNow.format(formatter)));
    }

    @Override
    protected void run(AssistActivity act, String location) {
        throw badCommand(R.string.error_unfinished_feature);
        // TODO: locations, time-elapse
    }

}
