package net.emilla.command.core;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.lang.Lang;
import net.emilla.time.TimeZone;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

final class Time extends CoreCommand {
    @internal Time(Context ctx) {
        super(ctx, CoreEntry.TIME, EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run(AssistActivity act) {
        giveTime(act, TimeZone.LOCAL);
    }

    @Override
    protected void run(AssistActivity act, String location) {
        var zone = TimeZone.of(Lang.EN_US, location);
        if (zone == null) {
            fail(act, R.string.error_invalid_time_zone);
            return;
        }

        giveTime(act, zone);
    }

    private void giveTime(AssistActivity act, TimeZone zone) {
        var now = LocalTime.now(zone.id());
        var formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        String time = now.format(formatter);
        var res = act.getResources();
        var zoneName = res.getString(zone.name);
        giveText(act, res.getString(R.string.zoned_time, time, zoneName));
    }
}
